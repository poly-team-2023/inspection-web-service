#!/bin/bash
#!/bin/sh

# Start vault
vault server -config vault.json &
sleep 3
echo "Vault started"


# Check if Vault is initialized
if vault status | grep -q "Initialized\s*true"; then
    echo "Vault has been uninitialized."
else
    echo "Vault is not initialized."
    vault operator init > generated_keys.txt
fi

# Parse unsealed keys
keyArray=$(grep "Unseal Key " < generated_keys.txt | cut -c15-)
vault operator unseal "$(echo "$keyArray" | awk 'NR==1')"
vault operator unseal "$(echo "$keyArray" | awk 'NR==2')"
vault operator unseal "$(echo "$keyArray" | awk 'NR==3')"

# Get root token
rootToken=$(grep "Initial Root Token: " < generated_keys.txt | cut -c21-)
echo "$rootToken" > root_token.txt

export VAULT_TOKEN="$rootToken"

# Enable kv
vault secrets enable -version=1 kv

# Enable userpass and add default user
#vault auth enable userpass
#vault policy write spring-policy spring-policy.hcl
#vault write auth/userpass/users/admin password="${SECRET_PASS}" policies=spring-policy

secrets_file="secrets.txt"
while IFS= read -r line; do
    if [[ "$line" != "#"* ]]; then
        vault kv put kv/my-secret $line
    fi
done < "$secrets_file"

# Keep the script running to keep the Vault server alive
trap "exit 0" SIGINT SIGTERM
wait