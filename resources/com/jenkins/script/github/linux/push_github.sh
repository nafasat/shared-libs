#!/bin/bash
USERNAME="$1"
PASSWORD="$2"
commit_msg="$3"
archive_name="$4"
repo_name_without_https="$5"
git clone https://"${USERNAME}":"${PASSWORD}"@"${repo_name_without_https}"
repo_name_only=$(basename "$repo_name_without_https" .git)
if [ -f ${archive_name} ]
then
  unzip -o "${archive_name}"
else
  echo "File doesn't exist ${archive_name}"
  exit 1
fi
main_file_name=`unzip -Z1 "${archive_name}"`
alias cp='cp'
if [[ `unzip -Zl "${archive_name}" | grep 'unx' | cut -f1 -d' '` =~ d+ ]]
then
  main_file_name=`unzip -Z1 $archive_name | head -1 | sed 's/\/$//g'`
  unzip -o "${archive_name}" -d tmp_"${main_file_name}"
  rm -rf "${archive_name}"
  cp -r tmp_"${main_file_name}"/"${main_file_name}"/* ./"${repo_name_only}"/
else
  unzip -o "${archive_name}"
  rm -rf "${archive_name}"
  cp "${main_file_name}" ./"${repo_name_only}"/
fi
cd ./"${repo_name_only}"
git checkout dtesting
git add -A
git commit -m "${commit_msg}"
git push https://"${USERNAME}":"${PASSWORD}"@"${repo_name_without_https}"
