import { defineStore } from "pinia";
import axios from '@axios'
import { isEmpty } from "@/@core/utils";
interface Template {
    templateId:number,
    templateName: string,
    templateContent: string,
    templateRegId: number,
    headerText:string,
    senderId:string,
    msgType:string
}
export const useTemplateStore= defineStore('template',()=>{
        const templateList = ref<Template[]>([])
        const templatePage=ref(4)
        const channelType=ref()
        const loadingTemplates=ref(false)
        const pageNumber=ref(0)
        const templateName=ref()
        const templateId =ref('')
        const isTemplateLastPage=ref(false)
        async function fetchTemplates(type: string) {
            try {
                loadingTemplates.value=true
                if (type == 'Search') pageNumber.value = 0;
               else pageNumber.value = pageNumber.value + 1;
                const resp = await axios.get('/api/campaigns/approved-templates', {
                    params: {
                        'pageNumber': pageNumber.value,
                        'pageSize':10,
                        'templateName': templateName.value,
                        'channelType':channelType.value
                    }
                })
                
                if (type == 'Search') templateList.value = resp.data.content
                else templateList.value = [...templateList.value, ...(resp).data.content] //adding to existing list
                loadingTemplates.value=false
                if (resp.data.last) //is this page is last
                {
                    isTemplateLastPage.value = true;
                }
                else isTemplateLastPage.value = false;
        
            } catch (err) {
        
            }
        }
        const templates=computed(()=>{
            let templates=[];
            if(!isEmpty(templateId.value)){
                if(channelType.value=='WhatsApp'){
                templates=[...templates,...(templateList.value.filter(e => e.templateId ==templateId.value ))]
               return templates.concat(templateList.value.filter(e => e.templateId!=templateId.value)).slice(0,4)
                }
                else if(channelType.value=='SMS'){
                    templates=[...templates,...(templateList.value.filter(e => e.templateRegId ==templateId.value ))]
                      return templates.concat(templateList.value.filter(e => e.templateRegId!=templateId.value)).slice(0,4)
                }
                else if(channelType.value=='Email'){
                    templates=[...templates,...(templateList.value.filter(e => e.templateId ==templateId.value ))]
                      return templates.concat(templateList.value.filter(e => e.templateId!=templateId.value)).slice(0,4)
                }
            }
             return templateList.value.slice(0,4)
        })
        return{
            templateList,
            channelType,
            templatePage,
            fetchTemplates,
            templateName,
            isTemplateLastPage,
            templates,
            templateId
        }
})
