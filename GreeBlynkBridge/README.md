# GreeBlynkBridge

As its name suggests, Gree Blynk Bridge is a small service written in C# using .NET Core 2.0 that connects a Blynk application to the Gree air conditioner units on the local network.

## Creating the database

1. Open a terminal
2. Navigate to the folder where `GreeBlynkBridge.csproj` can be found
3. Run the following commands:

```
dotnet restore
dotnet add package Microsoft.EntityFrameworkCore.Sqlite
dotnet add package Microsoft.EntityFrameworkCore.Design
dotnet ef migrations add InitialCreate
dotnet ef database update
```

At this point you should have a file named `GreeBlynkBridge.db`. This file will be copied automatically to the output folder during the build process.

## Building

1. Be sure you are still in the folder where `GreeBlynkBridge.csproj` resides
2. Issue the build command:

```
dotnet build -c Release
```

## Publishing

1. Be sure you are still in the folder where `GreeBlynkBridge.csproj` resides
2. Issue the publishing command:

```
dotnet publish -c Release --output Publish
```

If everything went fine you should find all the necessary files in the  `Publish` folder.

## Configuring the service

You can find an example configuration file next to the `csproj` called `config.json.example`. Copy this file next to the executable and rename it to `config.json`. After that you should edit the necessary parameters (e.g. set the network broadcast address(es) to the right values).

## Running the service

If you have successfully finished the configuration and database creation steps just issue the following command:

```
dotnet GreeBlynkBridge.dll
```
