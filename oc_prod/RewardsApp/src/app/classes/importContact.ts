import { User } from './user';
import { LookUp } from './lookup';
import { Header } from './header';
import { Report } from './report';
export class ImportContact
{
    header:Header
    importType:string
    lookup:LookUp
    report:Report
    user:User
}