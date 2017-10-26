job("Dev-builder-custom-service") {
    logRotator(-1, 10)
    label("common")

  scm {
      git {
      remote {
        credentials 'xxx-xxxx-xxxxx-xxxxxxx'
        url "ssh://git@bitbucket.xxxxx.com/glp/custom-service-repo.git"
             }
       branch "develop"
       extensions {
                 cloneOptions {
                             timeout(30)
                              }
                   }


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
  steps {

      shell('sudo npm install \n sudo npm run build-dev-linux \n mkdir docker_image \
            \n unzip ./consul.zip \n cp envconsul docker_image \n cp -a dist docker_image \n cp -a scripts/* docker_image/')
      shell ('echo ${BUILD_NUMBER}_$(date +"%d-%m-%Y_%H%M") > version.txt')
          }

  steps {

        configure {
        it / 'builders' << 'org.jenkinsci.plugins.buildnameupdater.BuildNameUpdater' {
        buildName 'version.txt'
        fromFile 'true'
                    }
                  }


   dockerBuildAndPublish {
        repositoryName("custom-service")
        tag('${BUILD_DISPLAY_NAME}')
        dockerRegistryURL("https://91111111111.dkr.ecr.us-east-1.amazonaws.com/custom-service")
        createFingerprints(true)
        buildContext('$WORKSPACE/docker_image')
        forceTag(false)
        forcePull(false)
        skipDecorate(true)

        }

    downstreamParameterized {
         trigger("Dev-deployer-SERVICENAME") {

           parameters {
               predefinedProp('buildVersion', '${BUILD_DISPLAY_NAME}')
                      }
                                               }
                             }

      }

    configure {
        it / 'builders' << 'hudson.plugins.sonar.SonarRunnerBuilder' {
          properties ''
          javaOpts ''
          jdk '(Inherit From Job)'
          project 'sonar.properties'
          task ''
        }
      }

  publishers {
        findbugs('**/findbugs-result.xml', false)
        extendedEmail {
            recipientList('xxxx@xx.com')
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
                        recipientList()
                    }
                }
            }
        }

  }


}



job("QA-builder-custom-service") {
    logRotator(-1, 10)
  label("generic")

  scm {
      git {
      remote {
        credentials 'xxx-xxxx-xxxxx-xxxxxxx'
        url "ssh://git@bitbucket.xxxxx.com/glp/custom-service-repo.git"
             }
       branch "master"
       extensions {
                 cloneOptions {
                             timeout(30)
                              }
                   }


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
steps {

    shell('sudo npm install \n sudo npm run build-qa-linux \n mkdir docker_image \
          \n unzip ./consul.zip \n cp envconsul docker_image \n cp -a dist docker_image \n cp -a scripts/* docker_image/')
    shell ('echo ${BUILD_NUMBER}_$(date +"%d-%m-%Y_%H%M") > version.txt')
        }


          steps {

              configure {
                  it / 'builders' << 'org.jenkinsci.plugins.buildnameupdater.BuildNameUpdater' {
                    buildName 'version.txt'
                    fromFile 'true'
                        }
                       }


     dockerBuildAndPublish {
          repositoryName("custom-service")
          tag('${BUILD_DISPLAY_NAME}')
          dockerRegistryURL("https://91111111111.dkr.ecr.us-east-1.amazonaws.com/custom-service")
          createFingerprints(true)
          buildContext('$WORKSPACE/docker_image')
          forceTag(false)
          forcePull(false)
          skipDecorate(true)

      }

      downstreamParameterized {
           trigger("QA-deployer-SERVICENAME") {
             parameters {
             predefinedProp('buildVersion', '${BUILD_DISPLAY_NAME}')
                        }
                                }
                     }

      }


  publishers {
        findbugs('**/findbugs-result.xml', false)
        extendedEmail {
            recipientList('xxxxx@xxx.com')
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
                        recipientList()
                    }
                }
            }
        }

  }


}
