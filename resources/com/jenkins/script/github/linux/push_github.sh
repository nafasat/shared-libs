USERNAME="$1"
PASSWORD="$2"
commit_msg="$3"
jenkins_user_email="$4"
jenkins_user="$5"
archive_name="$6"
repo_name_without_https="$7"
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
repo_name_only=$(basename "$7" .git)
alias cp='cp'
cp "${main_file_name}" ./"${repo_name_only}"/
cd ./"${repo_name_only}"
git checkout dtesting
git add -A
git config --global user.email "${jenkins_user_email}"
git config --global user.name "${jenkins_user}"
git commit -m "${commit_msg}"
git push https://"${USERNAME}":"${PASSWORD}"@"${repo_name_without_https}"
