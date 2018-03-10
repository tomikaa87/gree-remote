using Microsoft.Extensions.Logging;

namespace GreeBlynkBridge.Logging
{
    static class Logger
    {
        static ILoggerFactory s_loggerFactory = new LoggerFactory();

        static Logger()
        {
            s_loggerFactory.AddProvider(new ConsoleLoggerProvider());
        }

        public static ILogger CreateDefaultLogger()
        {
            return s_loggerFactory.CreateLogger("GreeBlynkBridge");
        }

        public static ILogger CreateLogger(string category)
        {
            return s_loggerFactory.CreateLogger(category);
        }

        public static ILogger<T> CreateLogger<T>()
        {
            return s_loggerFactory.CreateLogger<T>();
        }
    }
}
