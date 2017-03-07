 #! /bin/bash
 # this bash file reads in a line from a file one by one, and then executes the commands in the while loop body
 # until the last line of the file is reached.
 while IFS='' read -r line ; do
     echo "$line"
     kamctl add "$line" "$line"
 done < "$1"
