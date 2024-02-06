#!/bin/bash

# Start vault
vault server -config vault.json &
sleep 2
echo "Vault started"

# Check if Vault is initialized
if vault status | grep -q "Initialized\s*true"; then
    echo "Vault has been initialized."
else
    echo "Vault is not uninitialized."
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

## Put key-values from file
secrets_file="secrets.txt"
secret_string=""
while IFS= read -r line || [ -n "$line" ]; do
    line="${line%$'\r'}"
    if [[ "$line" != "#"* ]]; then
        secret_string="$secret_string $line"
    fi
done < "$secrets_file"
vault kv put kv/base $secret_string

# Keep the script running to keep the Vault server alive
trap "exit 0" SIGINT SIGTERM
wait