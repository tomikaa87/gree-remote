namespace GreeBlynkBridge.Logging
{
    using Microsoft.Extensions.Logging;

    internal static class Logger
    {
        private static ILoggerFactory loggerFactory = new LoggerFactory();

        static Logger()
        {
            loggerFactory.AddProvider(new ConsoleLoggerProvider());
        }

        public static ILogger CreateDefaultLogger()
        {
            return loggerFactory.CreateLogger("GreeBlynkBridge");
        }

        public static ILogger CreateLogger(string category)
        {
            return loggerFactory.CreateLogger(category);
        }

        public static ILogger<T> CreateLogger<T>()
        {
            return loggerFactory.CreateLogger<T>();
        }
    }
}
