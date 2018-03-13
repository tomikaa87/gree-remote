namespace GreeBlynkBridge.Logging
{
    using Microsoft.Extensions.Logging;

    internal class ConsoleLoggerProvider : ILoggerProvider
    {
        public ILogger CreateLogger(string categoryName)
        {
            return new ConsoleLogger(categoryName);
        }

        public void Dispose()
        {
        }
    }
}
