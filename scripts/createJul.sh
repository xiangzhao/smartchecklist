# This script assumes that a .jul file is given as the first argument
# and another directory is given as the second argument to the script.
# It then creates a new .jul file by combining the contents of the
# given .jul file and directory. The name of the new jul file starts with
# the name of the original .jul file and ends in the suffix "_new" .
# 
# This script should be run from the same directory as the given .jul file.

dirSuffix=_tempDir
dirForJul=$1$dirSuffix	# The directory where the given jul file will be extracted to
rm -r $dirForJul	# Remove this directory, if it exists
mkdir $dirForJul
echo \* Created a temporary directory $dirForJul.

cd $dirForJul		# Change into the new directory, because the subsequent jar
			# command will extract the given file into the current directory
jar -xf ../$1
echo \* Extracted $1 into the directory $dirForJul.

cp -r ../$2 .		# If it is necessary to add additional directories to the new jul file.
					# this line can be replicated and additional arguments be given to the script.
					# E.g., cp -r ../$3 etc. 
echo \* Copied $2 into the directory $dirForJul.

newJul=$1_new
jar -cf $newJul .
cp $newJul ../$newJul
echo \* Created file $newJul in the current directory. This file contains the contents of the original $1 and $2.

cd ..
rm -r $dirForJul	# Clean up by removing the temp directory created earlier by this script.
echo \* Removing the temp directory $dirForJul.