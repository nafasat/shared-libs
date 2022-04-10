def push_github_auth_based(Map github_args = [:]) {
  withCredentials([usernamePassword(credentialsId: "${github_args.credential_github_name}", usernameVariable: 'GITHUB_USERNAME', passwordVariable: 'GITHUB_PASSWORD')]) {
    withCredentials([usernamePassword(credentialsId: "${sftp_args.credential_sftp_name}", usernameVariable: 'SFTP_USERNAME', passwordVariable: 'SFTP_PASSWORD')]) {
      dir("${WORKSPACE}/")
      {
        if ("${sftp_args.sftp_path}" == '') {
          sftp_args.sftp_path='.'
        }
        sh "echo get ${sftp_args.sftp_path}/${sftp_args.tar_archive_name} | sshpass -p ${SFTP_PASSWORD} sftp -q -oStrictHostKeyChecking=no ${SFTP_USERNAME}@${sftp_args.sftp_ip}"
    
        if ("${github_args.commit_msg}" == '') {
          println("commit_msg is blank so passing Build_TAG with commit message")
          github_args.commit_msg="${BUILD_TAG}"
        }


        if ("${github_args.repo_name_without_https}" == '') {
          println("Please pass GitHub repo URL without HTTPS")
          currentBuild.result = 'ABORTED'
          error("Aborting the build.")  
        } else {
          repo_name_only = sh(returnStdout: true, script: "basename ${github_args.repo_name_without_https} .git").trim()
        }

        if ("${github_args.pull_from_branch_name}" == "master" || "${github_args.pull_from_branch_name}" == "main" || "${github_args.pull_from_branch_name}" == "") {
          def return_status_git_clone = sh returnStatus: true, script: "git clone https://${GITHUB_USERNAME}:${GITHUB_PASSWORD}@${github_args.repo_name_without_https}"
          if ("${return_status_git_clone}" == "0" ) {
            println("Git Repo Clone done from branch - origin")
          } else {
            println("Git Clone failed, Please check")
            currentBuild.result = 'ABORTED'
            error("Aborting the build.")
          }

        } else {
          def return_status_git_clone = sh returnStatus: true, script: "git clone -b ${github_args.pull_from_branch_name} https://${GITHUB_USERNAME}:${GITHUB_PASSWORD}@${github_args.repo_name_without_https}"
          if ("${return_status_git_clone}" == "0" ) {
            println("Git Repo Clone done from Branch - ${github_args.pull_from_branch_name}")
          } else {
            println("Git Clone failed, Please check")
            currentBuild.result = 'ABORTED'
            error("Aborting the build.")        
          }      
        }

        if ("${github_args.zip_file_name}" == '') {
          println("Please pass zip file name")
          currentBuild.result = 'ABORTED'
          error("Aborting the build.")      
        } else {
          return_status_zip_file_check = sh returnStatus: true, script: "ls ${github_args.zip_file_name}"
          if ("${return_status_zip_file_check}" == "0" ) {
            file_content_type = sh(returnStdout: true, script: "unzip -Zl ${github_args.zip_file_name} | grep 'unx' | head -1 |cut -f1 -d' '").trim()
            if ( file_content_type ==~ "^d.*" ) {
              main_file_name = sh(returnStdout: true, script: "unzip -Z1 ${github_args.zip_file_name} | head -1 | sed 's:/*\$::'").trim()
              println("${main_file_name} is a Dir")
              sh("unzip -o ${github_args.zip_file_name} -d tmp_${main_file_name}")
              sh("rm -rf ${github_args.zip_file_name}")
              status_copy_files_to_local_repo = sh(returnStatus: true, script: "cp -r tmp_${main_file_name}/${main_file_name}/* ./${repo_name_only}/")
              if ("${status_copy_files_to_local_repo}" == "0") {
                println("Done: coplied to local repo repo folder")
              } else {
                println("failed : coplied to local repo repo folder")
              }
            } else {
              main_file_name=sh(returnStdout: true, script: "unzip -Z1 ${github_args.zip_file_name}").trim()
              sh("unzip -o ${github_args.zip_file_name}")
              sh("rm -rf ${github_args.zip_file_name}")
              status_copy_files_to_local_repo = sh(returnStatus: true, script: "cp ${main_file_name} ./${repo_name_only}/")
              if ("${status_copy_files_to_local_repo}" == "0") {
                println("Done: coplied to local repo repo folder")
              } else {
                println("failed : coplied to local repo repo folder")
              }
            }
          } else {
            println("ZIP File doesn't exist at Workspace")
            currentBuild.result = 'ABORTED'
            error("Aborting the build.")          
          }
        }

        dir("${WORKSPACE}/${repo_name_only}") {
          if("${github_args.push_to_feature_branch_name}" == '') {
            git_checkout_feature_status = sh(returnStatus: true, script: "git checkout ${JOB_NAME}-${BUILD_ID}")
            if ("${git_checkout_feature_status}" != "0" ) {
              println("Such Feature branch ${JOB_NAME}-${BUILD_ID} doesn't exist, creating now")
              sh("git checkout -b ${JOB_NAME}-${BUILD_ID}")
            }
          } else {
            git_checkout_feature_status = sh(returnStatus: true, script: "git checkout ${github_args.push_to_feature_branch_name}")
            if ("${git_checkout_feature_status}" != "0" ) {
              println("Such Feature branch ${github_args.push_to_feature_branch_name} doesn't exist, creating now")
              sh("git checkout -b ${github_args.push_to_feature_branch_name}")
            }
          }

          sh("git add -A")
          sh("git commit -m ${github_args.commit_msg}")
          git_push_return = sh(returnStatus: true, script: "git push https://${GITHUB_USERNAME}:${GITHUB_PASSWORD}@${repo_name_without_https}")
          if ("${git_push_return}" == "0") {
            println("Change pushed done")
          } else {
            println("There is some issue to pushing change to GitHub")
          }
        }

        dir("${WORKSPACE}") {
          sh("rm -rf ${repo_name_only}")
        }
      }
    }
  }
}
