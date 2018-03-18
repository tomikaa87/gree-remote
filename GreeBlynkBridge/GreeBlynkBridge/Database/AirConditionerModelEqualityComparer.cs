namespace GreeBlynkBridge.Database
{
    using System.Collections.Generic;

    internal class AirConditionerModelEqualityComparer : IEqualityComparer<AirConditionerModel>
    {
        public bool Equals(AirConditionerModel x, AirConditionerModel y)
        {
            if (x == null || y == null)
            {
                return x == y;
            }

            return x.Equals(y);
        }

        public int GetHashCode(AirConditionerModel obj)
        {
            if (obj == null)
            {
                return 0;
            }

            return obj.GetHashCode();
        }
    }
}