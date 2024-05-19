import { defineStore } from "pinia";
import Segment from './SegmentsStep.vue'
import axios from "@axios";
import { isEmpty } from "@/@core/utils";
interface Segment {
    segRuleName: string,
    description: string,
    segRuleId: number,
    totEmailSize: number,
    totMobileSize: number,
    totSize: number,
    type: string,
    selected:boolean
}
export const useSegmentsStore=defineStore('segments',()=>{
    const segmentList=ref<Segment[]>([])
    const segmentPage=ref(0)
    const channelType=ref('')
    const searchByName=ref()
    const isSegmentLastPage=ref(false)
    const prevSelectedIds=ref(new Set())
    const segments=ref<Segment[]>([])
    const totalSelectedCount=computed(()=>{
        let count=0
        
        segmentList.value.forEach(seg => {
            if(seg.selected) { 
                if(channelType.value!='Email'){
                    count+=seg.totMobileSize
                }else {
                    count+=seg.totEmailSize
                }
            }

        })
        
        return count;
    })
    const selectedSegments=computed(()=>{
        if(segmentList.value.length >0 && prevSelectedIds.value.size >0)
        { //after intializing the list we set prevselected 
            console.log(prevSelectedIds.value)
            setSelectedSegments()
        }
        return segmentList.value.filter(e =>{
            return e.selected
        })
    })
    const segmentType=computed(()=>{
       return selectedSegments.value.length > 1?'Various' :selectedSegments.value.length==1?'Manual':'-'
    })
    const isSegmentSelected=computed(()=>{
        return selectedSegments.value.length > 0 
    })
    const segmentNames=computed(()=>{
        
        const names= selectedSegments.value.map(e => e.segRuleName)
         if(names.length==0) return '-'
         if(names.length >1) return names[0]+' +'+(names.length-1)
         return names.join(',')
    })
    const selectedIds=computed(()=>{
        return selectedSegments.value.map(seg => seg.segRuleId).join(',')
    })
    function setSelectedSegments(){
        
        if(prevSelectedIds.value.size ==0) return;
        const segIds=prevSelectedIds.value
         segmentList.value.forEach(seg =>{
            segIds.forEach(id =>{
                if(seg.segRuleId == Number(id)){
                    prevSelectedIds.value.delete(id)
                    seg.selected=true;
                }
            })
        })
        

    }
    async function fetchSegmentList(searchtype:string){
        try {
            
            if (searchtype == 'Search') {
                segments.value=segmentList.value.filter(seg => { return seg.segRuleName.toLowerCase().includes(searchByName.value.toLowerCase())})
            } 
            // else segmentPage.value = segmentPage.value + 1; //after for loadmore
            else{
                if(isEmpty(searchtype)) segmentPage.value = 0;
                if(searchtype=='Load More') segmentPage.value = segmentPage.value + 1;
                const resp = await axios.get('/api/segment/segment-list', {
                    params: {
                        'pageNumber': segmentPage.value,
                    }
                })
                console.log(resp)
                if(isEmpty(searchtype)){
                    segmentList.value = resp.data.content}
                else if(searchtype=='Load More'){ //adding to existing list
                 segmentList.value = [...segmentList.value, ...(resp).data.content]
                } 
                if(isEmpty(searchByName.value)){
                    segments.value=segmentList.value
                }
                else {
                    segments.value=segmentList.value.filter(seg => { return seg.segRuleName.toLowerCase().includes(searchByName.value.toLowerCase())})
                }
                if (resp.data.last) //is this page is last
                {
                    isSegmentLastPage.value = true;
                }
                else isSegmentLastPage.value = false;
            }
        } catch (err) {
            
        }

    }
    return {
        segmentList,
        searchByName,
        segmentPage,
        segments,
        fetchSegmentList,
        isSegmentLastPage,
        totalSelectedCount,
        selectedSegments,
        setSelectedSegments ,// for showing prev selection
        segmentType,
        segmentNames,
        isSegmentSelected,
        selectedIds,
        prevSelectedIds,
        channelType
    }
})
