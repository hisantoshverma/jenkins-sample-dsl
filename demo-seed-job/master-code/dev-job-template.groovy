// This file creates two Jobs,deployer and builder
// check bottom part of this file for builder job code.
// =================================================

job("Dev-deployer-SERVICENAME") {

    parameters {
    stringParam('buildVersion', 'latest')
    stringParam('COMMIT_ID', 'NoChange')
               }
    logRotator(-1, 10)
    label("deployer-slave")
    scm {
      git {
         remote {
         // Replace your git url and Jenkins credential id
         credentials 'xxxx-xxxx-xxxx-xxxx'
         url 'ssh://git@bitbucket.xxxxx.com/my-code.git'
                }
      branch 'refs/heads/master'

           }
      wrappers {
        preBuildCleanup()
        environmentDashboard {
            environmentName('AWS-DEV')
            componentName('SERVICENAME')
            buildNumber('$buildVersion')
            packageName('BITBUCKETURL')
            addColumns(true)
                column('COMMIT_ID', '$COMMIT_ID')
                             }
              }
         }
        // Below are the sample deploy step, please replace as per your requirements
    steps {
        shell('ansible-playbook ansible-code/playbooks/SERVICENAME.yml --extra-vars "env=dev region=us-west-2 ecs_action=update build_no=${buildVersion}"')
        shell ('if [ "$buildVersion" == "latest" ] \n then \
        \n echo "Latest_$BUILD_NUMBER" > build_version.txt \
        \n else \n echo $buildVersion > build_version.txt \n fi')

          }

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
            recipientList('my-email-id@globallogic.com')
            defaultSubject('$DEFAULT_SUBJECT')
            defaultContent('$DEFAULT_CONTENT')
            contentType('text/html')
            triggers {
                    always {
                    subject('$DEFAULT_SUBJECT')
                    content('$DEFAULT_CONTENT')
                         sendTo {
                           developers()
                           requester()
                           culprits()
                                }
                           }
                      }
                   }
                }

}



job("Dev-builder-SERVICENAME") {
    logRotator(-1, 10)
    label("build-slave")

    scm {
      git {
      remote {
       // Replace your git url and Jenkins credential id
        credentials 'xxxx-xxxx-xxxx-xxxx-xxxxx'
        url "ssh://git@bitbucket.xxxxxxx.com/glp/BITBUCKETURL.git"
             }
       branch "develop"


          }
        }


    triggers {
       configure {
        it / 'triggers' << 'com.cloudbees.jenkins.GitHubPushTrigger'{
        spec''
                   }
    scm('')
                 }
             }

    wrappers {
        preBuildCleanup()
             }
          // Below are the sample builds steps, please replace as per your requirements
    steps {
        maven('clean deploy -e -U -Dnexus.repo=https://devops-tools.my-nexus.com')
        shell('mkdir docker_image \n cp target/*exec.jar docker_image/ \n cp -a config docker_image/ \n cp scripts/Dockerfile docker_image/')
        shell ('echo ${BUILD_NUMBER}_$(date +"%d-%m-%Y_%H%M") > version.txt')
          }

  // Step adding to update build name with time stamp //
    steps {
        configure {
            it / 'builders' << 'org.jenkinsci.plugins.buildnameupdater.BuildNameUpdater' {
              buildName 'version.txt'
              fromFile 'true'
                       }
                  }

    dockerBuildAndPublish {
            repositoryName("dev-SERVICENAME")
            tag('${BUILD_DISPLAY_NAME}')
            dockerRegistryURL("https://xxxxxxxxx.dkr.ecr.us-east-1.amazonaws.com/dev-SERVICENAME")
            createFingerprints(true)
            buildContext('$WORKSPACE/docker_image')
            forceTag(false)
            forcePull(false)
            skipDecorate(true)

            }


       downstreamParameterized {
            trigger("Dev-ECS-Deployer-SERVICENAME") {
                block {
                    buildStepFailure('FAILURE')
                    failure('FAILURE')
                    unstable('UNSTABLE')
                      }
              parameters {
                    predefinedProp('buildVersion', '${BUILD_DISPLAY_NAME}')
                    predefinedProp('COMMIT_ID', '${GIT_COMMIT}')
				                 }
				                                            }
				                       }

            }

    configure {
        it / 'builders' << 'hudson.plugins.sonar.SonarRunnerBuilder' {
          properties ''
          javaOpts ''
          jdk '(Inherit From Job)'
          project 'config/sonar.properties'
          task ''
        }
             }

     publishers {
//        findbugs('**/findbugs-result.xml', false)
        extendedEmail {
            recipientList('my-email-id@globallogic.com')
            defaultSubject('$DEFAULT_SUBJECT')
            defaultContent('$DEFAULT_CONTENT')
            contentType('text/html')
            triggers {
                    always {
                    subject('$DEFAULT_SUBJECT')
                    content('$DEFAULT_CONTENT \nBuild tag:- $BUILD_DISPLAY_NAME')
                       sendTo {
                           developers()
                           requester()
                           culprits()
                           recipientList()
                              }
                      }
                     }
                      }

                }


}
