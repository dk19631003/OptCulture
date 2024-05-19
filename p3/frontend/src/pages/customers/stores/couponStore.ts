import { defineStore } from "pinia";
import { isEmpty } from "@/@core/utils";
import axios from "@axios";

interface Coupon {
    coupCodeStatus: string
    coupCodeExpiryDate: string
    couponExpiryDate: string
    couponStatus: string
    couponCode: string
    description: string
    issuedOn: string
    expiryDetails: string
}
export const useCouponStore=defineStore('issuedCoupons',()=>{
    const couponList=ref<Coupon[]>([])
    const filterCouponList=ref<Coupon[]>([])
    const totalIssuedCoupons = ref(0)
    const totalPages = ref(0)
    const pageNumber = ref(1)
    const pageSize = ref(3)
    const searchQuery = ref('')
    const appliedType = ref(false);
    const lapsedType = ref(false);
    const activeType = ref(false);
    const loadingCoupons=ref(false)
    async function loadCoupons(contactId:string) {

        try {
            if (isEmpty(contactId)) return;
            loadingCoupons.value=true
            const resp = await axios.get('/api/coupons/issued', {
                params: {
                    "contactId": contactId
                }
            })
            couponList.value = resp.data.object;
            console.log(resp)
            loadingCoupons.value=false
            filterCouponsBySearch();
        }
        catch (error) { console.log(error) }
    
    }
    function filterCouponsBySearch() { // filter coupons by search value and types
        filterCouponList.value = couponList.value;
        if (!isEmpty(searchQuery.value)) {
            filterCouponList.value = couponList.value.filter(coupon => (coupon.couponCode.toLowerCase().includes(searchQuery.value.toLowerCase())))
        }
        filterCouponList.value = filterCouponList.value.filter(coupon => {
            if (appliedType.value) {
                return coupon.coupCodeStatus.toLowerCase().includes('Redeemed'.toLowerCase())
            }
            if (lapsedType.value) {
                return (coupon.coupCodeStatus.toLowerCase().includes('Expired'.toLowerCase()) || (coupon.couponStatus.toLowerCase().includes('Expired'.toLowerCase())))
            }
            if (activeType.value) {
                return (coupon.coupCodeStatus.toLowerCase().includes('Active'.toLowerCase()) && coupon.couponStatus.toLowerCase().includes('Running'.toLowerCase()))
            }
            else return true;
        })
        //pagination
        totalIssuedCoupons.value = filterCouponList.value.length;
        totalPages.value = Math.ceil(totalIssuedCoupons.value / pageSize.value);
        const startIndex = (pageNumber.value - 1) * pageSize.value;
        const endIndex = startIndex + pageSize.value;
        filterCouponList.value = filterCouponList.value.slice(startIndex, endIndex);
    }
    watch(couponList,()=>{
        filterCouponsBySearch();
    },{deep:true})
    watch(pageNumber, () => {
        filterCouponsBySearch();
    })
    return{
        couponList,
        loadCoupons,
        loadingCoupons,
        pageNumber,
        totalPages,
        activeType,
        appliedType,
        lapsedType,
        filterCouponList,
        searchQuery,
        filterCouponsBySearch,
    }
})