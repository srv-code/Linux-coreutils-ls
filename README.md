# List 
## Synopsis
A utility program to show the directory and file listing.

## Features
- Option to show in human understandable format i.e. shows symbols for each file types, shows where the symlink points to and shows the file sizes in the highest memory unit possible (e.g. GB, MB, KB and B).
- Option to show the listing in long format i.e. shows each line for each file with its details such as permission, owner, type, size, modification date and time and name.
- Option to show hidden files also.
- Option to show the listing in recursive manner i.e. for each directory the whole directory tree is shown.
- Option to sort the list by a specified attribute (valid attributes to mention here are name, size, modified time, type).
- Option to reverse the sorting order by the specified attribute.

### Default behavior
- Shows the listing of the current directory if no path is mentioned.
- The human understandable format is turned off i.e. no symbols are shown for different file types, symbolic links are not resolved, file sizes are shown in bytes.
- The list details are not shown, only the file names will be listed.
- Hidden files are not shown.
- Lisiting recursively is turned off.
- Lists are sorted or reverse-sorted based on their names.