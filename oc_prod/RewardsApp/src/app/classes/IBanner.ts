import { Image } from './Image';

export interface IBanner {      
    "id": number
    "Title": string
    "Url": string
    "Description": string
    "Order": number
    "created_at": number
    "updated_at": number
    "Image" : Image 
}  