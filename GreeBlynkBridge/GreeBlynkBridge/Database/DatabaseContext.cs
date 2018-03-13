namespace GreeBlynkBridge.Database
{
    using Microsoft.EntityFrameworkCore;

    internal class DatabaseContext : DbContext
    {
        public DbSet<AirConditionerModel> AirConditioners { get; set; }

        protected override void OnConfiguring(DbContextOptionsBuilder optionsBuilder)
        {
            optionsBuilder.UseSqlite("Data Source=GreeBlynkBridge.db");
        }
    }
}
