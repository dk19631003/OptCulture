<script>
import OrCondition from "./OrCondition.vue";
import AndCondition from "./AndCondition.vue";

export default {
  components: {
    OrCondition,
    AndCondition,
  },
  props: {
    title: String,
    showAndText: Boolean,
    reference: String,
    segmentRules: Array,
    genratedString: String,
    code: String,
    couponCodes: Array,
    campaignNames: Array,
    tier: Array,
    loyaltyTiers: Array,
    udfs: Array,
    selectedUdf: Array,
    purchaseudfs: Array,
    editedPurchaseUDF: Array
  },
  data() {
    const orConditions = this.generateOrCondition();
    const andConditions = this.generateAndCondition();
    return {
      orConditions: orConditions,
      andConditions: andConditions,
      and_index: 0,
      or_index: orConditions.length,
      panelModel: [0],
      or_con_array: [],
      and_orcon_array: [],
      removeRule: false,
      text: "Please fill in the first set of fields before adding more conditions.",
      showError: false,
      andOrConditions: [],
      conditionsFilled: false,
      panelOpen: true,
      tierIndex: 0
    };
  },
  computed: {
    shouldRenderAndCondition() {
      return (reference, index) => {
        if (reference == 4) {
          return reference == 4 && index == 0;
        }
        return true;
      };
    }
  },

  methods: {
    /*ADD NEW OR RULE */
    addOrCondition(reference) {
      for (let i = 0; i < this.orConditions.length; i++) {
        this.orConditions[i].is_valid = false;
      }
      this.orConditions.push({
        or_index: this.or_index++,
        panel_id: reference,
        is_valid: false
      });
    },
    /*REMOVE OR RULE */
    removeOrCondition(orIndex, rule_id) {
      const secondLastIndex = this.orConditions.length - 2;
      if (this.orConditions[secondLastIndex].or_index == orIndex) {
        this.updateValidation(orIndex, true)
      }
      const indexToRemove = this.orConditions.findIndex(
        (panel) => panel.or_index === orIndex
      );
      const indexToRemoveOr = this.or_con_array.findIndex(
        (panel) => panel.or_index === orIndex
      );

      if (indexToRemove !== -1) {
        this.orConditions.splice(indexToRemove, 1);
      }

      if (indexToRemoveOr !== -1) {
        this.or_con_array.splice(indexToRemoveOr, 1);
      }
      this.$emit("removeSelectedOrRules", orIndex, rule_id);
    },
    /*ADD NEW AND RULE */

    addAndCondition(reference) {
      const newIndex = this.andConditions.length;
      console.log(newIndex, 'newIndex', this.conditionsFilled)

      if (newIndex === 0) {
        this.andConditions.push({
          and_index: newIndex,
          panel_id: reference,
        });
      } else {
        console.log('and-or-array', this.and_orcon_array.length)
        if (this.and_orcon_array.length == 0 || this.conditionsFilled == false) {
          this.showError = true;
        } else if (this.conditionsFilled == true) {
          this.showError = false;

        } else if (newIndex != this.and_orcon_array.length) {
          this.showError = true;
        }
        else {
          for (const item of this.and_orcon_array) {
            if (!item.values || !Array.isArray(item.values) || item.values.length === 0) {
              this.showError = true;
              break;
            } else {
              const firstValue = item.values[0];
              if (firstValue.rule_name === '' || firstValue.rule_string_value === '') {
                this.showError = true;
                break;
              }
            }
          }
        }

        if (!this.showError) {
          this.andConditions.push({
            and_index: newIndex,
            panel_id: reference,
          });
        }
      }
    },
    /*OPEN REMOVE RULES PANEL*/
    confirmRemoveRule() {
      this.removeRule = true;
    },
    /*REMOVE RULES PANEL*/
    removePanel(reference) {
      this.or_con_array = this.or_con_array.filter((item) => {
        return !(item.rule_id === reference);
      });

      this.and_orcon_array = this.and_orcon_array.filter((item) => {
        return !(item.rule_id === reference);
      });
      this.$emit("remove-panel", reference);
    },
    /*PASS OR DATA TO ID COMPONENT*/
    handleOrSelection(selectionData) {
      if (selectionData && selectionData.values && selectionData.values.length > 0) {
        selectionData.values.reduce((result, value) => {
          if (value.rule_name && value.rule_string_value) {
            if (this.orConditions.length == 2) {
              const lastIndex = this.orConditions.length - 1;
              if (lastIndex >= 0) {
                this.orConditions[lastIndex].is_valid = true;
              }
            } else {
              const secondLastIndex = this.orConditions.length - 2;
              if (this.orConditions[secondLastIndex].or_index == selectionData.or_index) {
                this.updateValidation(selectionData.or_index, true)
              }

            }
          } else {
            for (let i = 0; i < this.orConditions.length; i++) {
              this.orConditions[i].is_valid = false;
            }
          }
        }, {});

        const { or_index, rule_id, values } = selectionData;
        const existingEntryIndex = this.or_con_array.findIndex(
          (entry) => entry.or_index === or_index
        );

        if (existingEntryIndex !== -1) {
          this.or_con_array[existingEntryIndex] = { or_index, rule_id, values };
        } else {
          this.or_con_array.push({ or_index, rule_id, values });
        }
        this.$emit("getSelectedOrRules", this.or_con_array);
      }
    },

    /*Update validation INDEX */
    updateValidation(orIndexToUpdate, valid) {
      const indexToUpdate = this.orConditions.findIndex(item => item.or_index === orIndexToUpdate);
      if (indexToUpdate !== -1 && indexToUpdate < this.orConditions.length - 1) {
        // Update the is_valid property of the next object to true
        this.orConditions[indexToUpdate + 1].is_valid = valid;
        // Update the is_valid property to false for other objects
        for (let i = 0; i < this.orConditions.length; i++) {
          if (i !== indexToUpdate + 1) {
            this.orConditions[i].is_valid = false;
          }
        }
      }
    },

    /*PASS AND DATA TO ID COMPONENT*/
    handleAndSelection(selectionData, andIndex, andOrIndex) {
      console.log(selectionData, 'condition2')
      const { and_con_index, and_or_index, rule_id, values } = selectionData;
      // Find existing entry by both index and and_con_index
      const existingEntryIndex = this.and_orcon_array.findIndex(
        (entry) =>
          entry.and_or_index === and_or_index &&
          entry.and_con_index === and_con_index
      );
      if (existingEntryIndex !== -1) {
        this.and_orcon_array[existingEntryIndex] = {
          and_con_index,
          and_or_index,
          rule_id,
          values,
        };
      } else {
        this.and_orcon_array.push({
          and_con_index,
          and_or_index,
          rule_id,
          values,
        });
      }
      this.$emit("getSelectedAndRules", this.and_orcon_array);
      this.$emit("validation", selectionData.validation)
    },
    /*REMOVE AND OR CONDITION */
    removeAndSelection(andIndex, andOrIndex, rule_id) {
      this.and_orcon_array = this.and_orcon_array.filter((item) => {
        const conditionMatch = item.and_con_index == andIndex && item.and_or_index == andOrIndex;
        console.log('Condition Match:', conditionMatch);
        console.log('Item:', item);
        console.log('andIndex:', andIndex);
        console.log('andOrIndex:', andOrIndex);
        return !conditionMatch;
      });
      this.$emit("removeSelectedAndRules", andIndex, andOrIndex, rule_id);
      //   this.$emit("getSelectedAndRules", this.and_orcon_array);
    },
    /* GENRATE OR CONDITION AS PER GETSTRING   */
    generateOrCondition() {
      const getString = this.genratedString;
      if (getString && this.rule_id != 4) {
        let resultAndsplit = "";

        if (getString.includes("||")) {
          let splitString = getString.split("||");
          resultAndsplit = splitString[0];
        } else {
          resultAndsplit = getString;
        }

        let splitOr = resultAndsplit.split("<OR>");
        const orStringResult = splitOr.map((condition, index) => {
          const [rule_name, rule_key, rule_condition, rule_value, rule_ex_1] =
            condition.split("|");
          return {
            or_index: index,
            panel_id: this.reference,
            rule_name,
            rule_key,
            rule_condition,
            rule_value,
            rule_ex_1,
          };
        });
        return orStringResult;
      } else {
        return [
          { or_index: 0, panel_id: this.reference, is_valid: false },
          { or_index: 1, panel_id: this.reference, is_valid: false },
        ];
      }
    },
    /* GENRATE AND CONDITION AS PER GETSTRING   */
    generateAndCondition() {
      const getString = this.genratedString;
      if (getString && getString.includes("||") || getString && getString.includes("<OR>")) {
        let splitString = getString.split("||") || getString.split("<OR>");
        if (this.reference == 2) {
          splitString = splitString.splice(0);
        } else {
          splitString = splitString.slice(1);
        }
        const resultArray = splitString
          .map((andString, index) => ({
            and_index: index,
            panel_id: this.reference,
            and_strings: andString.trim(), // Trim to remove leading/trailing spaces
          }))
          .filter((item) => item.and_strings !== "");
        return resultArray;
      }
      return [];
    },

    async removeAndSet(index) {

      if (index > this.andConditions.length) {
        const lastIndex = this.andConditions.length - 1;
        const removedItem = this.andConditions.splice(lastIndex, 1)[0]; // Remove last item

        // Update and_index of the new last item
        if (lastIndex >= 0) {
          this.andConditions[lastIndex].and_index = lastIndex;
        }

        // Update and_index of the removed item
        removedItem.and_index = lastIndex;
      }
      else {
        const emitEventPromise = new Promise((resolve) => {
          this.$emit('updatedSelectedAndSegment', this.and_orcon_array[index].and_con_index ? this.and_orcon_array[index].and_con_index : index, this.reference);
          resolve();
        });
        emitEventPromise.then(() => {
          this.andConditions.splice(index, 1);
          this.and_orcon_array.splice(index, 1);
        });
        //    }
      }
      // this.$emit("getSelectedAndRules", this.and_orcon_array);
    },
    async removeOrSet(index) {
      if (index >= this.orConditions.length) {
        const lastIndex = this.orConditions.length - 1;
        const removedItem = this.orConditions.splice(lastIndex, 1)[0]; // Remove last item

        // Update and_index of the new last item
        if (lastIndex >= 0) {
          this.orConditions[lastIndex].or_index = lastIndex;
        }

        // Update and_index of the removed item
        removedItem.or_index = lastIndex;
      } else {
        this.orConditions.splice(index, 2); // Remove item at the specified index
        this.or_con_array.splice(index, 1)
      }
      this.$emit("getSelectedOrRules", this.or_con_array);
      this.$emit('updatedSelectedOrSegment', index, this.reference)
    },
    updateIndex(array) {
      this.andOrConditions = array
      console.log('andOrConditions', this.andOrConditions)
      this.$emit('andORConditions', this.andOrConditions)
    },
    checkOrConditionFilledChange(newVal) {
      console.log('changed', newVal)
      this.conditionsFilled = newVal
    },
    togglePanel() {
      this.panelOpen = !this.panelOpen;
      this.panelModel = [0]
    },
  },
  watch: {
    reference: {
      handler(newVal, oldVal) {
        if (newVal == '4' || newVal == '2') {
          setTimeout(() => {
            console.log(this.and_index, 'and-index', this.andConditions.length, this.and_orcon_array);
            if (this.and_orcon_array.length == 0) {
              this.addAndCondition(newVal);
            }
          }, 500);
        }
      },
      immediate: true
    },
    orConditions: {
      immediate: true,
      deep: true,
      handler() {
        this.$emit('orLength', this.orConditions.length)
      }
    },
  },
};
</script>

<template>
  <v-expansion-panels v-model="panelModel">
    <v-expansion-panel>
      <v-expansion-panel-title class="panel-open" style="flex: 1 0 100%; background-color: #dfcbec; height: 39px"
        @click="togglePanel">
        <div class="text-indigo-darken-2 text-subtitle-1 mr-3">{{ title }}</div>
        <template v-slot:actions>
          <v-icon color="#7367F0" :icon="panelOpen ? 'mdi-chevron-right' : 'mdi-chevron-right'"></v-icon>
        </template>
      </v-expansion-panel-title>
      <v-expansion-panel-text class="panel-border-set pr-5" v-show="panelOpen">
        <div class="pa-0 border-set">
          <!-- Default OR Condition -->
          <div class="float-end rounded pa-1 ps" style="background: #dfcbec; top: -15px; right: -15px">
            <v-icon icon="tabler-x" color="#7367F0" @click="confirmRemoveRule"></v-icon>
          </div>
          <!-- OR Condition -->
          <div class="panel-border border-bottom-0" v-if="reference != '4' && reference != '2'">
            <v-col cols="12" :class="index == 0 ? 'mt-5' : '' || reference == 4 ? 'pt-1' : 'pt-1 py-6 px-5'"
              v-for="(orCondition, index) in orConditions" :key="orCondition.or_index">
              <!-- OR RULES COMPONENT START-->
              <OrCondition :isLastOrCondition="index === orConditions.length - 1" :or_index="orCondition.or_index"
                :rule_id="reference" :orConditions="orCondition" :segmentRules="segmentRules"
                :isOrConditionFilled="orCondition.is_valid" @addOrCondition="addOrCondition(reference)"
                @remove=" removeOrCondition(orCondition.or_index, orCondition.panel_id)"
                @selectionChanged="handleOrSelection" :OrLength="orConditions.length" @removeOrSet="removeOrSet"
                :selectedTier="Object.keys(loyaltyTiers).find(key => loyaltyTiers[key] === orCondition.rule_value) || ''"
                :selectedUdf="udfs.find(key => key == orCondition.rule_name) || ''" :udfs="udfs"
                :purchaseudfs="purchaseudfs"
                :editedPurchaseUDF="purchaseudfs.find(key => key == orCondition.rule_name) || ''" />
              <!-- OR RULES COMPONENT END-->
            </v-col>
          </div>
          <div v-for="(andCondition, index) in andConditions" :key="andCondition.and_index"
            class="panel-border border-bottom-0">
            <!-- AND RULES COMPONENT -->
            <AndCondition :rule_id="reference" :segmentRules="segmentRules" :and_index="index" :andConditions="reference == 4 && andCondition.and_strings ? andConditions[0] && andConditions[0].and_strings && andConditions[0].and_strings.includes('cd.redeemed_on') ?
    andConditions[0].and_strings :
    andConditions[1] && andConditions[1].and_strings ?
      andConditions[1].and_strings : andCondition.and_strings : andCondition.and_strings"
              @selectionAndChanged="handleAndSelection" @selectionRemove="removeAndSelection" @removeSet="removeAndSet"
              :show="showError" :code="code" :couponCodes="couponCodes" :campaignNames="campaignNames"
              v-if="shouldRenderAndCondition(reference, index)" :loyaltyTiers="loyaltyTiers" :selectedTier="tier"
              @andORConditions="updateIndex" @orConditionFilledChanged="checkOrConditionFilledChange"
              :selectedUdf="selectedUdf" :udfs="udfs" :purchaseudfs="purchaseudfs"
              :editedPurchaseUDF="editedPurchaseUDF" />
          </div>

        </div>
        <!--NEW AND CONDITION ADD START -->
        <div class="d-flex justify-center" v-if="reference != '4' && reference != '2'">
          <v-btn prepend-icon="mdi-plus" size="large" rounded="lg" color="indigo-darken-1" variant="outlined"
            class="mt-3 mb-3 d-flex" @click="addAndCondition(reference)">
            <template v-slot:prepend>
              <v-icon color="#7367F0"></v-icon>
            </template>
            <v-text color="#7367F0"> AND condition</v-text>
          </v-btn>
        </div>
        <!-- AND CONDITION ADD END -->


      </v-expansion-panel-text>
    </v-expansion-panel>
    <!-- MIDDLE AND BETWEEN TWO AND CONDITION -->
    <div v-if="showAndText" class="d-flex justify-center mt-3 mb-3">
      <v-chip class="text-center rule-and-btn" label>
        <span class="text-white">AND</span>
      </v-chip>
    </div>

    <v-dialog v-model="removeRule" max-width="500px">
      <v-card>
        <v-card-title>Confirmation</v-card-title>
        <v-card-text>
          Are you sure you want to remove this rule?
        </v-card-text>
        <v-card-actions>
          <v-btn @click="removePanel(reference)">Yes</v-btn>
          <v-btn @click="removeRule = false">No</v-btn>
        </v-card-actions>
      </v-card>
    </v-dialog>
  </v-expansion-panels>
  <v-snackbar v-model="showError" :timeout="timeout" color="red-darken-1" location="top center">
    {{ text }}

    <template v-slot:actions>
      <v-btn color="black" variant="text" @click="showError = false">
        Close
      </v-btn>
    </template>
  </v-snackbar>
</template>

<style scoped>
.rule-bg-color {
  background-color: #685dd8;
}

.rule-bg-white {
  background-color: #f1f0f8;
}

.rule-or-btn {
  color: rgb(115, 103, 240);
  border: 1px solid white;
}

.rule-and-btn {
  width: 140px;
  height: 44px;
  background-color: #7367f0;
  font-size: 18px;
  align-items: center;
  display: grid;
}

.panel-border {
  border: 2px solid rgb(223, 203, 236);
}

.border-bottom-0 {
  border-bottom: unset;
}

.border-set .panel-border:last-child {
  border: 2px solid rgb(223, 203, 236);
}

.panel-border-set {
  margin-left: -18px;
  margin-right: -18px;
}

.panel-open {
  width: auto;
}

.last-or-condition {
  filter: blur(2px);
  /* Add the desired blur effect */
  pointer-events: none;
  /* Ma
  ke it unclickable */
}
</style>
