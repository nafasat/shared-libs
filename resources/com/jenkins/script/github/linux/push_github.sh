#!/bin/bash
USERNAME="$1"
PASSWORD="$2"
commit_msg="$3"
archive_name="$4"
repo_name_without_https="$5"
push_to_feature_branch_name="$6"
pull_from_branch_name="$7"
if [ -z $pull_from_branch_name ]
then
  git clone https://"${USERNAME}":"${PASSWORD}"@"${repo_name_without_https}"
else
  git clone -b ${pull_from_branch_name} https://"${USERNAME}":"${PASSWORD}"@"${repo_name_without_https}"
fi
repo_name_only=$(basename "$repo_name_without_https" .git)
alias cp='cp'
if [ -f ${archive_name} ]
then
  if [[ `unzip -Zl "${archive_name}" | grep 'unx' | cut -f1 -d' '` =~ d+ ]]
  then
    main_file_name=`unzip -Z1 $archive_name | head -1 | sed 's/\/$//g'`
    unzip -o "${archive_name}" -d tmp_"${main_file_name}"
    rm -rf "${archive_name}"
    cp -r tmp_"${main_file_name}"/"${main_file_name}"/* ./"${repo_name_only}"/
  else
    main_file_name=`unzip -Z1 "${archive_name}"`
    unzip -o "${archive_name}"
    rm -rf "${archive_name}"
    cp "${main_file_name}" ./"${repo_name_only}"/
  fi
else
    echo "File doesn't exist ${archive_name}"
  exit 1
fi
cd ./"${repo_name_only}"
if [ -z $push_to_feature_branch_name ]
then
  git checkout testing
  if [ $? -ne 0 ]
  then
    echo "Feature Branch testing doesn't exist, so creating and switching to it also"
    git checkout -b testing
  fi
  
else
  git checkout $push_to_feature_branch_name
  if [ $? -ne 0 ]
  then
    echo "Feature Branch doesn't exist, so creating and switching to it also"
    git checkout -b $push_to_feature_branch_name
  fi
fi
git add -A
git commit -m "${commit_msg}"
git push https://"${USERNAME}":"${PASSWORD}"@"${repo_name_without_https}"
