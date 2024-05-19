import { User } from './user';
import { Customer } from './customer';
import { MemberShip } from './member-ship';
import { Header } from './header';
export class LoyaltyInquiry
{
    header : Header
    membership : MemberShip
    customer : Customer
    user : User
}