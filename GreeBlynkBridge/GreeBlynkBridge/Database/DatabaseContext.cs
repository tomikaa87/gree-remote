using Microsoft.EntityFrameworkCore;

namespace GreeBlynkBridge.Database
{
    class DatabaseContext : DbContext
    {
        DbSet<AirConditionerModel> AirConditioners { get; set; }

        protected override void OnConfiguring(DbContextOptionsBuilder optionsBuilder)
        {
            optionsBuilder.UseSqlite("Data Source=GreeBlynkBridge.db");
        }
    }
}
