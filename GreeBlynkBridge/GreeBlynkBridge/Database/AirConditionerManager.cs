namespace GreeBlynkBridge.Database
{
    using System;
    using System.Collections.Generic;
    using System.Linq;
    using System.Threading.Tasks;
    using Microsoft.Extensions.Logging;

    internal static class AirConditionerManager
    {
        private static ILogger log = Logging.Logger.CreateLogger("AirConditionerManager");

        public static async Task UpdateAsync(AirConditionerModel model)
        {
            if (model == null)
            {
                throw new ArgumentException("Model parameter must not be null");
            }

            log.LogDebug($"Update: {model}");

            using (var db = new DatabaseContext())
            {
                var found = await db.FindAsync<AirConditionerModel>(model.ID);

                if (found == null)
                {
                    log.LogDebug("  model not found in the database, inserting");

                    await db.AddAsync(model);
                }
                else
                {
                    log.LogDebug("  model found in the database, checking if update needed");

                    if (found.Equals(model))
                    {
                        log.LogDebug("  model is up to date");
                    }
                    else
                    {
                        log.LogDebug("  model needs to be updated");

                        db.Remove(found);
                        if (await db.SaveChangesAsync() == 0)
                        {
                            log.LogError("  outdated model cannot be removed from the database");
                            return;
                        }

                        await db.AddAsync(model);
                    }
                }

                var savedRecords = await db.SaveChangesAsync();
                log.LogDebug($"  saved record(s): {savedRecords}");
            }
        }

        public static async Task<AirConditionerModel> GetByIDAsync(string id)
        {
            log.LogDebug($"GetByIDAsync: {id}");

            using (var db = new DatabaseContext())
            {
                var found = await db.FindAsync<AirConditionerModel>(id);

                log.LogDebug($"  found: {found?.ToString()}");

                return found;
            }
        }

        public static async Task<bool> RemoveByIDAsync(string id)
        {
            log.LogDebug($"RemoveByIDAsync: {id}");

            using (var db = new DatabaseContext())
            {
                var found = await db.FindAsync<AirConditionerModel>(id);

                if (found == null)
                {
                    log.LogWarning($"  cannot remove {id}, it's not found in the database");
                    return false;
                }

                db.Remove(found);

                if (await db.SaveChangesAsync() == 0)
                {
                    log.LogError("  cannot save changes");
                    return false;
                }
                else
                {
                    log.LogDebug("  removed from the database");
                }
            }

            return true;
        }

        public static ICollection<AirConditionerModel> LoadAll()
        {
            using (var db = new DatabaseContext())
            {
                 return db.AirConditioners.ToList();
            }
        }
    }
}
