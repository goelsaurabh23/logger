#Configuration </br>
`Logger` static class acts as a entry point for all public apis. Logger global configurations and current state is managed by `LoggerConfig` which includes various methods for setting, updating or resetting the configurations through key-value pairs. LoggerConfig `fromMap` method can be used for setting or changing previous configurations. </br>

Below is a sample code snippet which sets a route mapping for INFO level and File sink .

```
Map<String, String> params = new HashMap<String, String>();
params.put("ts_format", "dd­-mm­-yyyy-­hh-­mm-­ss");
params.put("log_level", "INFO");
params.put("sink_type", "FILE");
params.put("file_location", "/var/log/logger/info.log");
params.put("thread_model", "SINGLE");
params.put("write_mode", "SYNC");
LoggerConfig.fromMap(params);
```
`fromMap` method can be called multiple times with different configurations with newer one overriding the previous one. Below configuration will update the previous route mapping for INFO level to Console Sink.

```
Map<String, String> params = new HashMap<String, String>();
params.put("ts_format", "dd­-mm­-yyyy-­hh-­mm-­ss");
params.put("log_level", "INFO");
params.put("sink_type", "CONSOLE");
params.put("thread_model", "SINGLE");
params.put("write_mode", "SYNC");
LoggerConfig.fromMap(params);
```

#Routing</br>
`LoggerConfig` keeps a global hashMap of current route mapping of Level & Sink. `RouteInfo` class wraps timestamp format and sink info for a particular message level assuming timestamp format is dependent on message level instead of a sink. It handles the case where we might be using same sink for different message levels while timestamp info has to be formatted based on a message level.

Below configurations will result in different timestamp format for INFO & DEBUG message levels while using the same file based sink instance for both

```
Map<String, String> params = new HashMap<String, String>();
params.put("ts_format", "dd­-mm­-yyyy-­hh-­mm-­ss");
params.put("log_level", "INFO");
params.put("sink_type", "FILE");
params.put("file_location", "/var/log/logger/info.log");
params.put("thread_model", "SINGLE");
params.put("write_mode", "SYNC");
LoggerConfig.fromMap(params);
```
```
Map<String, String> params = new HashMap<String, String>();
params.put("ts_format", "dd­:mm­:yyyy:hh:­mm:­ss");
params.put("log_level", "DEBUG");
params.put("sink_type", "FILE");
params.put("file_location", "/var/log/logger/info.log");
params.put("thread_model", "SINGLE");
params.put("write_mode", "SYNC");
LoggerConfig.fromMap(params);
```

#LoggingEvent</br>
`Sink` receives `LoggingEvent` instance in their respective write methods which encapsulates `LogMessage` while adding additional info need for logging purposes like current timestamp, current thread name etc. `LogEngine` before routing log message creates a new instance of `LoggingEvent` while adding current timestamp info which will be passed around different components of the logger system allowing different components to add additional info before final logging

#Dynamic Sink Loading</br>
`Sink` can be loaded dynamically with different configurations. For dynamic loading to work please make sure below requirements are met

* All custom sinks should implement the Sink interface
* Every sink need to have a default parameter less constructor so that class object can be created dynamically
* For dynamic class loading to work, framework need to know the fully qualified name of the class like `com.saurabh.logger.sinks.FileSink` so that java reflections can be used. `Sink_class` property during configuration can be used for providing that info. In case `sink_class` property is not provided during configuration framework will try to search for the respective class based on `sink_type` property in `com.saurabh.logger.sinks` package. Currently framework provides support for `file`, `fileExtra` & `console` sink types. New `sink_type` will provided in future.

Below configuration will load the sink based on the `sink_class` property

```
Map<String, String> params = new HashMap<String, String>();
params.put("ts_format", "dd­:mm­:yyyy:hh:­mm:­ss");
params.put("log_level", "DEBUG");
params.put("sink_type", "FILE");
params.put("sink_class","com.saurabh.logger.sinks.FileSink");
params.put("file_location", "/var/log/logger/info.log");
params.put("thread_model", "SINGLE");
params.put("write_mode", "SYNC");
LoggerConfig.fromMap(params);
```

While below configuration will result in searching for `file` `sink_type` in `com.saurabh.logger.sinks` package

```
Map<String, String> params = new HashMap<String, String>();
params.put("ts_format", "dd­:mm­:yyyy:hh:­mm:­ss");
params.put("log_level", "DEBUG");
params.put("sink_type", "FILE");
params.put("file_location", "/var/log/logger/info.log");
params.put("thread_model", "SINGLE");
params.put("write_mode", "SYNC");
LoggerConfig.fromMap(params);
```
* After class object has been created, framework will set the required properties like `file_location` on the newly created instance through `MethodParam` annotation set on respective properties setter methods.Framework will map `MethodParam` `name` field with the provided configuration and convert the given property value to `type` field. Framework support properties set on super classes too, please check `FileExtraInfoSink` class for a better understanding

#Async Write Mode</br>
Framework supports asynchronous log message writing to different sinks through `AsyncSink` class.`AsyncSink` maintains a blocking queue of 256 buffer size which is used for storing `LoggingEvent` messages till they are consumed by different workers asynchronously. Worker threads consumes the messages from blocking queue and calls the respective `sink` write methods passing the `LoggingEvent` instance. `AsyncSink` class wraps the respective sink.

`AsyncSink` instance differentiate themselves from other instance based on the `Sink` they are currently wrapping. Below configurations will result in multiple instances of `AsyncSink` class since different file locations will result in different `FileSink` instances

```
Map<String, String> params = new HashMap<String, String>();;
params.put("ts_format", "dd­:mm­:yyyy:hh:­mm:­ss");
params.put("log_level", "INFO");
params.put("sink_type", "FILE");
params.put("file_location", "/var/log/logger/info.log");
params.put("thread_model", "MULTI");
params.put("write_mode", "ASYNC");
LoggerConfig.fromMap(params);

//Add new settings for debug level
params = new HashMap<String, String>();
params.put("ts_format", "dd­:mm­:yyyy:hh:­mm:­ss");
params.put("log_level", "DEBUG");
params.put("sink_type", "FILE");
params.put("file_location", "/var/log/logger/debug.log");
params.put("thread_model", "MULTI");
params.put("write_mode", "ASYNC");
LoggerConfig.fromMap(params);
```

#MessageFormatter</br>
Framework will format the messages based on `{ Datetime [ThreadName] Level nameSpace - content } ` format.
