def push_github_script(Map github_args = [:]) {
  withCredentials([usernamePassword(credentialsId: "${github_args.credential_github_name}", usernameVariable: 'USERNAME', passwordVariable: 'PASSWORD')]) {
    
    if ("${github_args.commit_msg}" == '') {
      println("commit_msg is blank so passing Build_TAG with commit message")
      github_args.commit_msg="${BUILD_TAG}"
    }


    if ("${github_args.repo_name_without_https}" == '') {
      println("Please pass GitHub repo URL without HTTPS")
      currentBuild.result = 'ABORTED'
      error("Aborting the build.")  
    } else {
      def repo_name_only = sh(returnStdout: true, script: "basename ${github_args.repo_name_without_https} .git").trim()
    }
    println("Line no 17 ${repo_name_only}")

    if ("${github_args.pull_from_branch_name}" == "master" || "${github_args.pull_from_branch_name}" == "main" || "${github_args.pull_from_branch_name}" == "") {
      def return_status_git_clone = sh returnStatus: true, script: "git clone https://${USERNAME}:${PASSWORD}@${github_args.repo_name_without_https}"
      if ("${return_status_git_clone}" == "0" ) {
        println("Git Repo Clone done from branch - origin")
      } else {
        println("Git Clone failed, Please check")
        currentBuild.result = 'ABORTED'
        error("Aborting the build.")
      }

    } else {
      def return_status_git_clone = sh returnStatus: true, script: "git clone -b ${github_args.pull_from_branch_name} https://${USERNAME}:${PASSWORD}@${github_args.repo_name_without_https}"
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
      def return_status_zip_file_check = sh returnStatus: true, script: "ls ${github_args.zip_file_name}"
      if ("${return_status_zip_file_check}" == "0" ) {
        def file_content_type = sh(returnStdout: true, script: "unzip -Zl ${github_args.zip_file_name} | grep 'unx' | cut -f1 -d' '").trim()
        if ( file_content_type ==~ "^d.*" ) {
          main_file_name = sh(returnStdout: true, script: "unzip -Z1 ${github_args.zip_file_name} | head -1 | sed 's:/*\$::'").trim()
          sh("unzip -o ${github_args.zip_file_name} -d tmp_${main_file_name}")
          sh("rm -rf ${github_args.zip_file_name}")
          println("Repo Name line 52 ${repo_name_only}")
          sh("cp -r tmp_${main_file_name}/${main_file_name}/* ./${repo_name_only}/")
        } else {
          main_file_name=sh(returnStdout: true, script: "unzip -Z1 ${github_args.zip_file_name}").trim()
          sh("unzip -o ${github_args.zip_file_name}")
          sh("rm -rf ${github_args.zip_file_name}")
          println("Line  no 58")
          println(println("Repo Name line 59 ${repo_name_only}"))
          println("cp ${main_file_name} ./${repo_name_only}/")
          sh("cp ${main_file_name} ./${repo_name_only}/")
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
      git_push_return = sh(returnStatus: true, script: "git push https://${USERNAME}:${PASSWORD}@${repo_name_without_https}")
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
