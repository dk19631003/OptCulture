<script>
import AndOrCondition from "./AndOrCondition.vue";
export default {
  components: {
    AndOrCondition,
  },
  props: {
    rule_id: Number,
    segmentRules: Array,
    and_index: Number,
    andConditions: Array,
    code: String,
    couponCodes: Array,
    campaignNames: Array,
    segment_rule: Array,
    selectedTier: Array,
    loyaltyTiers: Array,
    udfs: Array,
    selectedUdf: Array,
    purchaseudfs: Array,
    editedPurchaseUDF: String
  },
  data() {
    const andOrConditions = this.rule_id == 4 || this.rule_id == 2 ? [this.generateAndOrCondition()[0]] : this.generateAndOrCondition();
    return {
      andOrConditions: andOrConditions,
      and_or_index: andOrConditions.length,
      and_or_con_array: [],
    };
  },
  methods: {
    /*ADD NEW AND  CONDITION*/
    addAndOrCondition(rule_id, and_index) {
      for (let i = 0; i < this.andOrConditions.length; i++) {
        this.andOrConditions[i].is_valid = false;
      }
      this.andOrConditions.push({
        and_con_index: and_index,
        and_or_index: this.and_or_index++,
        panel_id: rule_id,
        is_valid: false
      });
    },
    /*REMOVE AND  CONDITION*/
    removeAndOrCondition(andOrIndex, andIndex, rule_id) {
      const secondLastIndex = this.andOrConditions.length - 2;
      if (this.andOrConditions[secondLastIndex].and_or_index == andOrIndex) {
        this.updateValidation(andOrIndex, true);
      }
      const indexToRemove = this.andOrConditions.findIndex(
        (panel) => panel.and_or_index == andOrIndex
      );
      if (indexToRemove != -1) {
        this.andOrConditions.splice(indexToRemove, 1);

        this.$emit("selectionRemove", andIndex, andOrIndex, rule_id);
      }
      //  this.$emit('orConditionFilledChanged', this.andOrConditions[this.andOrConditions.length - 1].is_valid);
    },
    /*PASS DATA TO RULES COMPONENT*/
    handleSelection(selectionData) {
      if (selectionData && selectionData.values && selectionData.values.length > 0) {
        selectionData.values.reduce((result, value) => {
          if (value.rule_name === 'Gender' || value.rule_name === 'Contact Source' ||
            value.rule_operation === 'Empty' || value.rule_operation === 'Today' ||
            value.rule_string_value) {
            if (this.andOrConditions.length == 2) {
              const lastIndex = this.andOrConditions.length - 1;
              if (lastIndex >= 0) {
                this.andOrConditions[lastIndex].is_valid = true;
              }
            } if (this.andOrConditions.length >= 2) {
              const secondLastIndex = this.andOrConditions.length - 2;
              if (this.andOrConditions[secondLastIndex].and_or_index == selectionData.and_or_index) {
                this.updateValidation(selectionData.and_or_index, true);
              }
            }

          } else {
            for (let i = 0; i < this.andOrConditions.length; i++) {
              this.andOrConditions[i].is_valid = false;
            }
          }
        }, {});

      }
      this.$emit("selectionAndChanged", selectionData);
    },

    removeAnd(index) {
      this.$emit("removeSet", index)
    },

    /*Update validation INDEX */
    updateValidation(orIndexToUpdate, valid) {
      const indexToUpdate = this.andOrConditions.findIndex(item => item.and_or_index == orIndexToUpdate);
      if (indexToUpdate != -1 && indexToUpdate < this.andOrConditions.length - 1) {
        // Update the is_valid property of the next object to true
        this.andOrConditions[indexToUpdate + 1].is_valid = valid;

        // Update the is_valid property to false for other objects
        for (let i = 0; i < this.andOrConditions.length; i++) {
          if (i != indexToUpdate + 1) {
            this.andOrConditions[i].is_valid = false;
          }
        }
      }
    },

    /* GENRATE AND OR  CONDITION AS PER GET STRING   */
    generateAndOrCondition() {
      if (this.andConditions) {
        let getAndString = this.andConditions;
        let splitOr = getAndString.split("<OR>");
        let orStringResult = splitOr.map((condition, index) => {
          const [rule_name, rule_key, rule_condition, rule_value, rule_ex_1] =
            condition.split("|");
          return {
            and_con_index: this.and_index,
            and_or_index: index,
            panel_id: this.rule_id,
            rule_name,
            rule_key,
            rule_condition,
            rule_value,
            rule_ex_1,
          };
        });
        return orStringResult;
      } else {
        if (this.rule_id == 4 || this.rule_id == 2) {
          return [
            {
              and_con_index: this.and_index,
              and_or_index: 0,
              panel_id: this.rule_id,
            },
          ];
        } else {
          return [
            {
              and_con_index: this.and_index,
              and_or_index: 0,
              panel_id: this.rule_id,
            },
            {
              and_con_index: this.and_index,
              and_or_index: 1,
              panel_id: this.rule_id,
            },
          ];
        }
      }
    },
    handleOrConditionFilledChange() {
      this.$emit('orConditionFilledChanged', this.andOrConditions[this.andOrConditions.length - 1].is_valid);

    }
  },
  watch: {
    andOrConditions: {
      immediate: true,
      deep: true,
      handler(newArray) {
        this.$emit('andORConditions', newArray);
      }
    }
  }
};
</script>

<template>
  <div>
    <div style="transform: translateY(-15px)" v-if="rule_id != 4 && rule_id != 2">
      <v-sheet style="background: transparent" class="d-flex justify-center">
        <v-chip size="small" variant="outlined" class="px-4 py-3 ml-3 rule-bg-color" label>
          <span class="text-subtitle-2 text-white">AND</span>
        </v-chip>
      </v-sheet>
    </div>
    <div class="py-2" v-else></div>
    <!-- AND OR Condition -->
    <v-col cols="12" class="pt-1 py-6" :class="index == 0 ? 'px-8 mt-1' : 'px-5'"
      v-for="(andOrCondition, index) in andOrConditions" :key="andOrCondition.and_or_index">
      <AndOrCondition :isLastAndOrCondition="index === andOrConditions.length - 1" :and_index="and_index"
        :and_or_index="andOrCondition.and_or_index" :rule_id="rule_id" :segmentRules="segmentRules"
        :andorConditions="andOrCondition" @addAndOrCondition="addAndOrCondition(rule_id, and_index)" @remove="
      removeAndOrCondition(
        andOrCondition.and_or_index,
        andOrCondition.and_con_index,
        andOrCondition.panel_id
      )
      " @selectionAndChanged="handleSelection" @removeAnd="removeAnd" :andOrLength="andOrConditions.length"
        :isOrConditionFilled="andOrCondition.is_valid" :code="code" :couponCodes="couponCodes"
        :campaignNames="campaignNames"
        :selectedTier="Object.keys(loyaltyTiers).find(key => loyaltyTiers[key] === andOrCondition.rule_value) || ''"
        @orConditionFilledChanged="handleOrConditionFilledChange" :udfs="udfs"
        :selectedUdf="udfs.find(key => key == andOrCondition.rule_name) || ''" :purchaseudfs="purchaseudfs"
        :editedPurchaseUDF="purchaseudfs.find(key => key == andOrCondition.rule_name)" />
    </v-col>
  </div>
</template>

<style scoped>
.rule-bg-color {
  background-color: #7367f0;
}

.rule-bg-white {
  background-color: #f1f0f8;
}

.last-or-condition {
  filter: blur(2px);
  /* Add the desired blur effect */
  pointer-events: none;
  /* Make it unclickable */
}
</style>
