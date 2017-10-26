job("Stage-deployer-SERVICENAME") {

     parameters {
     stringParam('buildVersion', 'latest')
                }

     logRotator(-1, 10)
     label("common")
     scm {
       git {
          remote {
          credentials 'xxxx-xxxxxxx-xxxxxx-xxx'
          url 'ssh://git@bitbucket.xxxxxx.com/my-code.git'
                 }
       branch 'refs/heads/master'

           }
         }

      wrappers {
        preBuildCleanup()
        environmentDashboard {
            environmentName('AWS-STG')
            componentName('SERVICENAME')
            buildNumber('$buildVersion')
                             }
          }

      steps {
              copyArtifacts('QA-deployer-SERVICENAME')
              {
              includePatterns('build_version.txt')
              buildSelector {
                             workspace()
                            }
               }

             shell('ansible-playbook playbooks/SERVICENAME.yml --extra-vars "env=stage region=us-east-1 ecs_action=update build_no=${buildVersion}"')
             shell ('if [ "$buildVersion" == "latest" ] \n then \
                    \n echo "Latest_$(cat build_version.txt)" > build_version.txt \
                    \n else \n echo $buildVersion > build_version.txt \nfi')

            }
      // Step adding to update build name with time stamp //
      steps {
         configure {
             it / 'builders' << 'org.jenkinsci.plugins.buildnameupdater.BuildNameUpdater' {
             buildName 'build_version.txt'
             fromFile 'true'
             }
                   }
            }


      publishers {
      extendedEmail {
            recipientList('my-email-id@xxxxx.com')
            defaultSubject('$DEFAULT_SUBJECT')
            defaultContent('$DEFAULT_CONTENT')
            contentType('text/html')
            triggers {
                    always {
                    subject('$DEFAULT_SUBJECT')
                    content('$DEFAULT_CONTENT \nBuild tag:- $BUILD_DISPLAY_NAME')
                    sendTo     {
                        developers()
                        culprits()
                        recipientList()
                               }
                           }
                     }
                   }
                 }


}
