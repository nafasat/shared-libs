#!/bin/bash
echo $PWD
USERNAME="$1"
PASSWORD="$2"
commit_msg="$3"
archive_name="$4"
repo_name_without_https="$5"
git clone https://"${USERNAME}":"${PASSWORD}"@"${repo_name_without_https}"
if [ -f ${archive_name} ]
then
  unzip -o "${archive_name}"
else
  echo "File doesn't exist ${archive_name}"
  exit 1
fi
main_file_name=`unzip -Z1 "${archive_name}"`
unzip -o "${archive_name}"
rm -rf "${archive_name}"
repo_name_only=$(basename "$repo_name_without_https" .git)
alias cp='cp'
cp "${main_file_name}" ./"${repo_name_only}"/
cd ./"${repo_name_only}"
git checkout dtesting
git add -A
git commit -m "${commit_msg}"
git push https://"${USERNAME}":"${PASSWORD}"@"${repo_name_without_https}"
