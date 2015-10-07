# XL Release Stress tests

There are two projects in this repository :

- Data Generator : an application that populates an XL Release instance with active, completed releases and templates.
- Runner : an application that connects to an XL Release instance and performs stress tests.

## Requirements

- Java 7 SDK
- XL Release 4.6.0 or greater

# Data Generator

The data generator should **not** be run against a production environment, as it will generate many active, completed and template releases.

It should be run only once against a newly installed XL Release instance, running it several times on the same XL Release instance will result in errors.


## Running the data generator

The application can be started with the following command :

    ./gradlew :data-generator:run [parameters]

or on windows

    gradlew :data-generator:run [parameters]

It uses the following optional parameters :

- **Server URL**: The URL of the XL Release server instance
    - Syntax : `-Pserver-url=http://url.to.server:5516`
    - The default value is `http://localhost:5516`
- **Username**: The username that will be used to connect to the server instance. This username needs "admin" permissions in order to populate data
    - Syntax : `-Pusername=admin`
    - The default value is `admin`
- **Password**: The password of the user account that will be used to connect to the server instance.
    - Syntax : `-Ppassword=password`
    - The default value is `admin`
- **Active Releases count**: The number of active releases that should be created.
    - Syntax : `-Pactive-releases=100`
    - The default value is `10`
- **Completed Releases count**: The number of completed releases that should be created.
    - Syntax : `-Pcompleted-releases=500`
    - The default value is `10`
- **Templates count**: The number of templates that should be created.
    - Syntax : `-Ptemplates=100`
    - The default value is `10`

Example :

    ./gradlew :data-generator:run -PbaseUrl=http://localhost:5516 -Pusername=admin -Ppassword=admin -Ptemplates=20 -Pactive-releases=20 -Pcompleted-releases=20

# Runner

The runner should **not** be run against a production environment.

It should be run against an XL Release Server on which the data-generator has already been run.

## Running

The application can be started with the following command :

    ./gradlew :runner:run [parameters]

or on windows

    gradlew :runner:run [parameters]

It uses the following optional parameters :

- **Server URL**: The URL of the XL Release server instance
    - Syntax : `-PbaseUrl=http://url.to.server:5516`
    - The default value is `http://localhost:5516`
- **Username**: The username that will be used to connect to the server instance. This username needs "admin" permissions in order to view all data
    - Syntax : `-Pusername=admin`
    - The default value is `admin`
- **Password**: The password of the user account that will be used to connect to the server instance.
    - Syntax : `-Ppassword=password`
    - The default value is `admin`
- **Simulation**: The simulations to execute (separated by a comma). If it is empty then `RealisticSimulation` will run.
    - Syntax :
        - `-Psimulation=stress.RealisticSimulation` or
        - `-Psimulation=stress.DevelopmentTeamSimulation,stress.OpsSimulation`
    - The possible values are :
        - `stress.DevelopmentTeamSimulation` : several development teams commit code which triggers new releases. Each teams consists of ~10 developers.
        - `stress.OpsSimulation` : several ops people are working with XL Release
        - `stress.ReleaseManagerSimulation` : several release managers are working with XL Release
        - `stress.RealisticSimulation` : A simulation which combines several roles of people working with XL Release in one realistic usage scenario.
    - The default value is `stress.RealisticSimulation`
- **Teams**: The number of development teams that will be running the `stress.DevelopmentTeamSimulation`
    - Syntax : `-Pteams=10`
    - The default value is `10`
- **Ops**: The number of "ops" users that will be running the `stress.OpsSimulation`
    - Syntax : `-Pops=20`
    - The default value is `20`
- **Release Managers**: The number of "ops" users that will be running the `stress.ReleaseManagerSimulation`
    - Syntax : `-PreleaseManagers=20`
    - The default value is `20`
-- **Ssh host**: Some simulations start a release which connects to a host using SSH. If you want these tasks to finish successfully, then you need to specify `-PsshHost=my-working-ssh-host-ip-address -PsshUser=username -PsshPassword=password`. The default value for `sshUser` is `ssh_test` and `sshPassword` is `ssh_test`.

Example:

    ./gradlew :runner:run -PbaseUrl=http://localhost:5516 -Psimulation=stress.RealisticSimulation -Pusername=admin -Ppassword=password

## Performances Reports

The performance reports are generated in the **runner/reports** directory. Each simulation execution will generate a separate report folder, you can browse there and open file `index.html` to view the Gatling report.


## Advanced configuration parameters

`xl.runner.durationDilation` - multiplies all the durations in the runner configuration by this value. 
