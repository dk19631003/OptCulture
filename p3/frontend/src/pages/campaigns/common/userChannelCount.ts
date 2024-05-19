import axios from "@axios";
import { count } from "console";
export async function fetchUserChannelsCount(channelType:string){
    let count=0;
    try{
       const resp= await axios.get('/api/user/channel-settings-count/'+channelType)
        // .then((resp)=>{
        //     console.log(resp.data)
        //     count=resp.data
        //     return count;
        // }).catch((err)=>console.log(err))
        count=resp.data
    }catch(err){
        console.log(err)
    }
    return count
}