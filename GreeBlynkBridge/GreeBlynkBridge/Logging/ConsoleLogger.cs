﻿using Microsoft.Extensions.Logging;
using System;

namespace GreeBlynkBridge.Logging
{
    class ConsoleLogger : ILogger
    {
        readonly string m_categoryName;

        public ConsoleLogger(string categoryName)
        {
            m_categoryName = categoryName;
        }

        public IDisposable BeginScope<TState>(TState state)
        {
            throw new NotImplementedException();
        }

        public bool IsEnabled(LogLevel logLevel)
        {
            throw new NotImplementedException();
        }

        public void Log<TState>(LogLevel logLevel, EventId eventId, TState state, Exception exception, Func<TState, Exception, string> formatter)
        {
            if (exception != null)
                throw new NotImplementedException("Exception logging is not implemented");

            var originalForegroundColor = Console.ForegroundColor;

            Console.Write("[");

            switch (logLevel)
            {
                case LogLevel.Trace:
                case LogLevel.Debug:
                case LogLevel.None:
                    Console.ForegroundColor = ConsoleColor.Cyan;
                    break;

                case LogLevel.Information:
                    Console.ForegroundColor = ConsoleColor.Green;
                    break;

                case LogLevel.Warning:
                    Console.ForegroundColor = ConsoleColor.Yellow;
                    break;

                case LogLevel.Error:
                    Console.ForegroundColor = ConsoleColor.Red;
                    break;

                case LogLevel.Critical:
                    Console.ForegroundColor = ConsoleColor.DarkMagenta;
                    break;
            }

            Console.Write(logLevel.ToString()[0]);
            Console.ForegroundColor = originalForegroundColor;
            Console.Write("]");

            Console.WriteLine($"[{m_categoryName}]: {state.ToString()}");
        }
    }
}