using Microsoft.Extensions.Logging;
using System;
using System.Collections.Generic;
using System.Threading.Tasks;
using System.Linq;

namespace GreeBlynkBridge.Database
{
    static class AirConditionerManager
    {
        static ILogger s_log = Logging.Logger.CreateLogger("AirConditionerManager");

        public static async Task UpdateAsync(AirConditionerModel model)
        {
            if (model == null)
                throw new ArgumentException("Model parameter must not be null");

            s_log.LogDebug($"Update: {model}");

            using (var db = new DatabaseContext())
            {
                var found = await db.FindAsync<AirConditionerModel>(model.ID);

                if (found == null)
                {
                    s_log.LogDebug("  model not found in the database, inserting");

                    await db.AddAsync(model);
                }
                else
                {
                    s_log.LogDebug("  model found in the database, checking if update needed");

                    if (found.Equals(model))
                    {
                        s_log.LogDebug("  model is up to date");
                    }
                    else
                    {
                        s_log.LogDebug("  model needs to be updated");

                        db.Remove(found);
                        if (await db.SaveChangesAsync() == 0)
                        {
                            s_log.LogError("  outdated model cannot be removed from the database");
                            return;
                        }

                        await db.AddAsync(model);
                    }
                }

                var savedRecords = await db.SaveChangesAsync();
                s_log.LogDebug($"  saved record(s): {savedRecords}");
            }
        }

        public static async Task<AirConditionerModel> GetByIDAsync(string id)
        {
            s_log.LogDebug($"GetByIDAsync: {id}");

            using (var db = new DatabaseContext())
            {
                var found = await db.FindAsync<AirConditionerModel>(id);

                s_log.LogDebug($"  found: {found?.ToString()}");

                return found;
            }
        }

        public static async Task<bool> RemoveByIDAsync(string id)
        {
            s_log.LogDebug($"RemoveByIDAsync: {id}");

            using (var db = new DatabaseContext())
            {
                var found = await db.FindAsync<AirConditionerModel>(id);

                if (found == null)
                {
                    s_log.LogWarning($"  cannot remove {id}, it's not found in the database");
                    return false;
                }

                db.Remove(found);

                if (await db.SaveChangesAsync() == 0)
                {
                    s_log.LogError("  cannot save changes");
                    return false;
                }
                else
                {
                    s_log.LogDebug("  removed from the database");
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
