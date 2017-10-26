listView('AWS-Dev-Build')
    {
    description('All Dev-Build')
    jobs {

       name('AWS-job build')
       regex(/Dev-builder-.*/)
    }

       columns {
        status()
        weather()
        name()
        lastSuccess()
        lastFailure()
        lastDuration()
        buildButton()
    }
    }

    listView('AWS-Dev-Deploy')
        {
        description('All Dev Deploy')
        jobs {

           name('All Dev Deploy')
           regex(/Dev-deployer.*/)
        }

           columns {
            status()
            weather()
            name()
            lastSuccess()
            lastFailure()
            lastDuration()
            buildButton()
        }
        }

   listView('AWS-Stage-Deploy')
            {
            description('All Stage Deploy')
            jobs {

               name('All Stage Deploy seed jobs')
               regex(/Stage-deployer.*/)
            }

               columns {
                status()
                weather()
                name()
                lastSuccess()
                lastFailure()
                lastDuration()
                buildButton()
            }
            }

 listView('AWS-QA-Build')
                     {
                     description('QA Build Jobs')
                     jobs {

                        name('QA Build Jobs')
                        regex(/QA-builder.*/)
                     }

                        columns {
                         status()
                         weather()
                         name()
                         lastSuccess()
                         lastFailure()
                         lastDuration()
                         buildButton()
                     }
                     }

 listView('AWS-QA-Deploy')
                 {
                 description('QA Deploy Jobs')
                jobs {

                    name('QA Deploy Jobs')
                    regex(/QA-deployer.*/)
                       }

                      columns {
                          status()
                          weather()
                          name()
                          lastSuccess()
                          lastFailure()
                          lastDuration()
                          buildButton()
                           }
                       }
