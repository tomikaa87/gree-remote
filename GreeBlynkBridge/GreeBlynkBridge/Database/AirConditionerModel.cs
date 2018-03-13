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
            {
                return false;
            }

            return this.ID == o.ID &&
                this.Name == o.Name &&
                this.PrivateKey == o.PrivateKey &&
                this.Address == o.Address;
        }

        public override int GetHashCode()
        {
            return this.ID.GetHashCode()
                ^ this.Name.GetHashCode()
                ^ this.PrivateKey.GetHashCode()
                ^ this.Address.GetHashCode();
        }
    }
}
