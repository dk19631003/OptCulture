import { defineStore } from "pinia";
import {ref,computed} from 'vue';
import {isEmpty} from '@/@core/utils';
import { dateValidator } from '@/@core/utils/validators'

export const useScheduleDataStore= defineStore('scheduleData',()=>{
    const scheduleType=ref('')
    const scheduleDate = ref()
    const recuScheduleTime = ref()
    const recuScheduleDate = ref('')
    const frequencyType = ref() //monthly,weekly,..
    const isRangeDateValid = ref(false)
    const startDate = ref()
    const endDate = ref()
    const isOneTimeDateValid = ref(false)
    const step3Completed=ref(false)
    const status=ref('')
    const isSentOut=ref(false)
    function intializeData(){
        scheduleDate.value= ''
        recuScheduleDate.value=''
        recuScheduleTime.value=''
        isOneTimeDateValid.value=false
        isRangeDateValid.value=false
        frequencyType.value=''
        endDate.value=''
        startDate.value=''
        scheduleType.value='ONE TIME'
    }
    function validateDate(val) {
        // console.log(val)
        if (isEmpty(val)) {
            isOneTimeDateValid.value = false;
            return 'Date required for scheduling the campaign'
        }
        
        else if (scheduleType.value == 'ONE TIME') { //onetime
            isOneTimeDateValid.value = dateValidator(val)
            if (isOneTimeDateValid.value) {
                return true
            };
            return 'Please select a future date and time'
        }
        else { //recurring type has two dates
            // console.log(val)
            const dates = recuScheduleDate.value.split('to');
            if (dates.length < 2 && dates.length > 0) {
                isRangeDateValid.value = false;
                return 'Please select schedule ending date also'
            }
            else {
                const date1 = dates[0].trim();
                const date2 = dates[1].trim();
    
                if (date1 == date2) return 'Starting and ending dates should not be same'
                if (isEmpty(recuScheduleTime.value)) return 'Please select schedule time'
                startDate.value = date1 + ' ' + recuScheduleTime.value;
                endDate.value = date2 + ' ' + recuScheduleTime.value;
                const valid1 = dateValidator(startDate.value);
                const valid2 = dateValidator(endDate.value);
                
                isRangeDateValid.value = valid1 && valid2
                // console.log(isRangeDateValid.value)
                if (!isRangeDateValid.value) {
                    return 'Please select a future dates and time'
                }
                return true;
            }
        }
    }
    const recDateValidation = computed(() => {
        // console.log(isRangeDateValid.value)
        return validateDate(recuScheduleDate.value);
    })
    watch(scheduleDate,()=>{
        validateDate(scheduleDate.value)
    })
    return {
    scheduleType,    
    scheduleDate, 
    recuScheduleTime ,
    recuScheduleDate ,
    frequencyType  ,
    isRangeDateValid, 
    startDate,
    endDate,
    intializeData,
    isOneTimeDateValid,
    step3Completed,
    status ,
    recDateValidation,
    validateDate,
    isSentOut //to check a campaign is sent 
    }
})
