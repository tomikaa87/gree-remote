using System.Collections.Generic;

namespace GreeBlynkBridge.Database
{
    public class AirConditionerModel
    {
        public string ID { get; set; }

        public string Name { get; set; }
        public string PrivateKey { get; set; }
        public string Address { get; set; }

        public override string ToString()
        {
            return $"AirConditionerModel(ID={ID}, Name={Name}, PrivateKey={PrivateKey}, Address={Address})";
        }

        public override bool Equals(object obj)
        {
            var o = obj as AirConditionerModel;

            if (o == null)
                return false;

            return ID == o.ID &&
                Name == o.Name &&
                PrivateKey == o.PrivateKey &&
                Address == o.Address;
        }

        public override int GetHashCode()
        {
            return ID.GetHashCode() ^ Name.GetHashCode() ^ PrivateKey.GetHashCode() ^ Address.GetHashCode();
        }
    }

    class AirConditionerModelEqualityComparer : IEqualityComparer<AirConditionerModel>
    {
        public bool Equals(AirConditionerModel x, AirConditionerModel y)
        {
            if (x == null || y == null)
                return x == y;

            return x.Equals(y);
        }

        public int GetHashCode(AirConditionerModel obj)
        {
            if (obj == null)
                return 0;

            return obj.GetHashCode();
        }
    }
}
