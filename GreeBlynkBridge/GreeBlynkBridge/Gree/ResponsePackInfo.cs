using Newtonsoft.Json;
using System;
using System.Collections.Generic;
using System.Text;

namespace GreeBlynkBridge.Gree
{
    /*
     {
       "t":"pack",
       "i":1,
       "uid":0,
       "cid":"f4911e1fb84e",
       "tcid":"dbf070a3dc18",
       "pack":"LP24Ek0OaYogxs3iQLjL4Ofip9Tx8sFnlIz0mxh1LI9YT1PXPBDouOPiWHaG4G8lz22fUZtUbkblUYF5BzlKDV9xeQzMlsaP4RKBtrnsDrPuzZLTv1r2r82SMfLJcKapI/Pa9syFI0kYFCUeBNxq44+UmYq4E5g5QzzU+6/Qd+RU3UC3aq81zKyYgPW18pBYkywWYwaOwxo65kKReDaAYU+t/W6ao6KLeI6AQs8sVr4ezcPY11b6/8Z6I/cSoqX+hIYi/BBoGa5JRIR7Vv8zxQ=="
    }
    */


    class ResponsePackInfo : PackInfo
    {
        [JsonProperty("uid")]
        public int UID { get; set; }

        [JsonProperty("tcid")]
        public string TargetClientId { get; set; }

        [JsonProperty("pack")]
        public string Pack { get; set; }
    }
}
