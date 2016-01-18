# XL TestView Stress tests

There are two projects in this repository :

- Data Generator : an application that populates an XL TestView instance with active, completed releases and templates. It also contains convenience functions used in the simulations.
- Runner : an application that connects to an XL TestView instance and performs stress tests.

## Requirements

- Java 7 SDK
- XL TestView 1.4.0 or greater

# Runner

The runner should **not** be run against a production environment.

It should be run against an XL TestView Server on which the data-generator has already been run.

## Running

The application can be started with the following command :

    ./gradlew :runner:run [parameters]

or on windows

    gradlew :runner:run [parameters]

The performance tests are configurable. Options can be set using the following parameters, or by changing the `runner.conf` file.

- **Server URL**: The URL of the XL TestView server instance
    - Syntax : `-Pxl.baseUrl=http://url.to.server:6516`
    - The default value is `http://localhost:6516`
- **Username**: The username that will be used to connect to the server instance. This username needs "admin" permissions in order to view all data
    - Syntax : `-Pxl.username=admin`
    - The default value is `admin`
- **Password**: The password of the user account that will be used to connect to the server instance.
    - Syntax : `-Pxl.password=password`
    - The default value is `admin`
- **Simulation**: The simulations to execute (separated by a comma). If it is empty then `RealisticSimulation` will run.
    - Syntax :
        - `-Pxl.simulation=stress.simulations.ProjectSimulation` or
        - `-Pxl.simulation=stress.simulations.DashboardSimulation,stress.simulations.ImportSimulation`
    - The possible values are :
        - `stress.simulations.ProjectSimulation` : Simulates a number of users creating projects.
        - `stress.simulations.DashboardSimulation` : Simulates a number of users browsing dashboards
        - `stress.simulations.ImportSimulation` : This simulation tests the parallel import of a lot of test results.
    - The default value is to run all simulations.

Example:

    ./gradlew :runner:run -Pxl.baseUrl=http://localhost:6516 -Pxl.simulation=stress.RealisticSimulation \
    -Pxl.username=admin -Pxl.password=password

### Individual options for simulations
The following options are available for all simulations:

- `sim.<name>.ramp-up-period = 10 seconds` - After initialising, time to wait
- `sim.<name>.post-warm-up-pause = 10 seconds`

Each simulation has its own options to set running parameters. These are in the `sim` object in the configuration file:

#### ImportSimulation
This simulation simulates the parallel import of a lot of test results.

- `sim.import-runs.parallel-test-specs = 5`: Number of test specifications that receive imports
- `sim.files-per-test-spec = 1000`: Number of files in each import
- `sim.rounds = 10`: Number of import per test specification

#### Dashboard simulation
Simulates a number of users browsing dashboards
- `stress.simulations.DashboardSimulation`
	- `sim.dashboards.users = 5`

#### Project simulation
Simulates a number of users creating projects
- `stress.simulations.ProjectSimulation`
	- `users = 5`


## Performances Reports

The performance reports are generated in the **runner/reports** directory. Each simulation execution will generate a separate report folder, you can browse there and open file `index.html` to view the Gatling report.

## Sending results to graphite or influxdb

It is possible to send realtime data to graphite or influxdb by adding the properties `-Pgatling.data.writers=console,file,graphite -Pgatling.data.graphite.host=my.monitor.host` to the gradle commandline.

## Advanced configuration parameters

`xl.runner.durationDilation` - multiplies all the durations in the runner configuration by this value.
