#!/bin/bash

# Specify the directory where you want to search for 0-byte files
directory="/home/hitesh/OC3/frontend/src/views/ereceipts"

# Use the find command to locate 0-byte files in the specified directory
# The -type f option ensures that we only consider regular files
# The -empty option checks for empty files (0 bytes)
zero_byte_files=$(find "$directory" -type f -empty)

# Loop through the list of zero-byte files and write the template into each file
for file in $zero_byte_files; do
  echo "<template>${file}</template>" > "$file"
done

# Optionally, you can print a message to indicate that the operation is complete
echo "Template insertion into 0-byte files is complete."
