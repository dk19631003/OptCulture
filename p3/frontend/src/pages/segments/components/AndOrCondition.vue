<script>
export default {
  props: {
    isLastAndOrCondition: Boolean,
    isOrConditionFilled: Boolean,
    and_index: Number,
    and_or_index: Number,
    segmentRules: Array,
    rule_id: Number,
    andorConditions: Array,
    andOrLength: Number,
    code: String,
    couponCodes: Array,
    campaignNames: Array,
    selectedTier: String,
    selectedUdf: String,
    udfs: Array,
    purchaseudfs: Array,
    editedPurchaseUDF: String


  },
  data() {
    return {
      showCol: true,
      currentIndex: null,
      selectedRuleName: null,
      selectedCondition: null,
      selectedOperation: null,
      selectedRuleNameKey: null,
      selectedRuleCondiKey: null,
      selecteOpeKey: null,
      selectTimePeriod: null,
      inputStringValue: "",
      inputDateOneValue: "",
      inputDateTwoValue: "",
      inputNumOneValue: "",
      inputNumTwoValue: "",
      inputNumThreeValue: "",
      timeout: 2000,
      text: "Please fill in the first set of fields before adding more conditions.",
      showError: false,
      selectedCondition2: null,
      discountCodes: [],
      showInputs: true,
      showCondition: true,
      campaignsList: [],
      selectedCampaigns: [],
      campaignIds: [],
      selectedCampaignIds: [],
      loyaltyTiers: [],
      selectAll: false,
      udfList: [],
      allUDf: {},
      purchaseudfList: [],
      purchaseUDFall: {}

    }
  },
  methods: {
    /* SHOW/HIDE INPUT AS PER CONDITION AND OPERATION */
    handleArray(sRule, sCondition, sRuleName) {
      const conditionRemovals = {
        "is not": ["Empty"],
        "is": ["Last range of days", "Next Range Of Days"],
        "is(in range)": [
          "Today",
          "Equal to",
          "Between",
          "On or after",
          "On or before",
          "After",
          "Before",
        ],
        "is(year ignored)": ["Empty"],
        "within last": ["remove"],
        "not within last": ["remove"],
        "days since last purchase": ["remove"],
        "Issued on": ["is in next"],
        "Redeemed on": ["is in next"],
        "Expiry on": ["within last", "not within last", "is (in range)", "any date", "never"]
      };

      const selectedOperationArray = sRule.rule_operation;
      if (
        conditionRemovals[sCondition] &&
        conditionRemovals[sCondition].includes("remove")
      ) {
        return [];
      }
      if (sRuleName == "Purchase Date" && sCondition == "is not") {
        return ["After", "Before"];
      } else if (
        sRuleName == "Anniversary" &&
        sCondition == "is(year ignored)"
      ) {
        return ["Last range of days", "Next Range Of Days"];
      } else if (
        sRuleName == "Loyalty Enrolled Date" &&
        sCondition == "is(in range)"
      ) {
        return ["Last range of days"];
      }

      // Filter out any unwanted values based on the condition
      const updatedOperationArray = selectedOperationArray.filter(
        (operation) => {
          return !(
            conditionRemovals[sCondition] &&
            conditionRemovals[sCondition].includes(operation)
          );
        }
      );
      return updatedOperationArray;
    },
    /*ADD NEW OR CONDITION*/
    addAndOrCondition() {
      if (this.isOrConditionFilled) {
        this.$emit("addAndOrCondition");
      } else {
        if (this.selectedRuleName == null) {
          this.showError = true;
        }
      }
    },
    /*REMOVE OR CONDITION*/
    onRemove() {
      this.$emit("remove");
      //  this.emitSelection()
    },
    removeSet(index) {
      this.$emit('removeAnd', this.and_index)
    },
    /*ON CHANGE OR CONDITION PASS TO AND COMPONENT*/
    emitSelection() {
      let selecteKey = this.segmentRules.find(
        (r) => r.rule_name == this.selectedRuleName
      );
      if (!selecteKey) {
        if (this.andorConditions) {
          selecteKey = findMatchingRule(this.segmentRules, this.andorConditions
          );
        }
      } else {
        selecteKey = selecteKey;
      }
      if (selecteKey && this.selectedCondition) {
        const rulename_string = selecteKey.rule_name_string[0];
        const selecteConKeyindex = selecteKey.rule_condition.indexOf(
          this.selectedCondition
        );
        if (selecteConKeyindex != -1) {
          this.selectedRuleNameKey = selecteKey.rule_name_key[selecteConKeyindex];
          this.selectedRuleCondiKey = selecteKey.rule_condition_key[selecteConKeyindex];
        }
        const selecteOpeKeyindex = selecteKey.rule_operation.indexOf(
          this.selectedOperation
        );

        let selecteOpeKey = "";
        if (selecteOpeKeyindex != -1) {
          this.showCol = true;
          this.selecteOpeKey = selecteKey.rule_operation_key[selecteOpeKeyindex];
          selecteOpeKey = this.selecteOpeKey;
        } else {
          this.selecteOpeKey = "";
          this.showCol = false;
        }

        let stringValue = "";
        if (selecteOpeKey == "text" || selecteOpeKey == "") {
          stringValue = this.inputStringValue;
        } else if (selecteOpeKey == "number") {
          stringValue = this.inputNumOneValue;
        } else if (selecteOpeKey == "none") {
          stringValue = "IS NULL";
        } else if (selecteOpeKey == "option") {
          /*CHANGE SELECTED OPTION VALUE */
          stringValue = this.selectedOperation;
          if (stringValue === "Manual Upload") {
            stringValue = "AddedManually";
          } else if (stringValue == "Web Form") {
            stringValue = "WebForm";
          } else if (stringValue == "eCommerce") {
            stringValue = "eComm";
          }
        } else if (selecteOpeKey == "date" || ['Equal to', 'On or after', 'On or before', 'After', 'Before'].includes(this.selectTimePeriod)) {
          stringValue = this.inputDateOneValue;
        } else if (selecteOpeKey == "date_range" || this.selectTimePeriod === 'Between') {
          if (this.inputDateOneValue && this.inputDateTwoValue) {
            stringValue = `${this.inputDateOneValue}|${this.inputDateTwoValue}`;
          }
        } else if (selecteOpeKey == "number_range") {
          if (this.inputNumOneValue && this.inputNumTwoValue) {
            stringValue = `${this.inputNumOneValue}|${this.inputNumTwoValue}`;
          }
        } else if (selecteOpeKey == "today") {
          stringValue = "Today";
        }

        if (
          this.selectedCondition == "within last" ||
          this.selectedCondition == "not within last" ||
          (['Issued on', 'Redeemed on', 'Expiry on'].includes(this.selectedCondition) && selecteOpeKey == "number_with")
        ) {
          let timePeriod = 0;
          if (this.selectTimePeriod === "days") {
            timePeriod = 1;
          } else if (this.selectTimePeriod === "weeks") {
            timePeriod = 7;
          } else if (this.selectTimePeriod === "months") {
            timePeriod = 30;
          }
          this.showCol = true; //hide collum when only text box
          if (this.inputNumThreeValue) {
            stringValue = this.inputNumThreeValue * timePeriod;
          }
        } else if (this.selectedCondition == "days since last purchase") {
          stringValue = this.inputNumTwoValue;
        } else if (
          this.selectedCondition == "is empty" ||
          this.selectedOperation == "Empty"
        ) {
          stringValue = "IS NULL";
        }
        if (['Issued on', 'Redeemed on', 'Expiry on'].includes(this.selectedCondition) && this.selectTimePeriod == "Today") {
          stringValue = this.selectTimePeriod;
        }
        /* CHANGE RULE KEY */
        if (
          this.selectedCondition == "Total" &&
          this.selectedOperation == "Amount"
        ) {
          this.selectedRuleNameKey = "loyalty.total_giftcard_amount";
        } else if (
          this.selectedCondition == "Balance" &&
          this.selectedOperation == "Points"
        ) {
          this.selectedRuleNameKey = "loyalty.loyalty_balance";
        } else if (
          this.selectedCondition == "Balance" &&
          this.selectedOperation == "Amount"
        ) {
          this.selectedRuleNameKey = "loyalty.giftcard_balance";
        }
        if (this.selectTimePeriod == "Between" && ['Points', 'Balance'].includes(this.selectedOperation)) {
          stringValue = '';
          if (this.inputNumOneValue && this.inputNumTwoValue) {
            stringValue = `${this.inputNumOneValue}|${this.inputNumTwoValue}`;
          }
        } else if (this.selectTimePeriod == "Empty") {
          stringValue = "IS NULL";
        }
        else if (this.selectedRuleName == "Loyalty Tier") {
          this.selectedRuleCondiKey = 'loyalty.program_tier_id'
          stringValue = `Number:=${this.selectedOperation}`;
        }
        if (rulename_string == 'C.UDF' && this.selectedCondition) {
          this.selectedRuleNameKey = this.allUDf[this.selectedCondition]
          this.selectedRuleCondiKey = 'String'
          console.log(this.selectedRuleCondiKey, 'condition-key')
        }
        if (rulename_string == 'P.UDF' && this.selectedCondition) {
          this.selectedRuleNameKey = this.purchaseUDFall[this.selectedCondition]
          this.selectedRuleCondiKey = 'String'
          console.log(this.selectedRuleNameKey, 'condition-key')
        }
        let selectedValuesArray;
        if (this.rule_id == 2) {
          selectedValuesArray = [
            {
              rule_type: "OR",
              rule_name: rulename_string,
              rule_name_key: this.selectedRuleNameKey,
              rule_condition_key: this.selectedRuleCondiKey,
              rule_condition: this.selectedCampaigns.map(item => item.key),
              rule_operation: this.selectedOperation,
              rule_operation_key: selecteOpeKey,
              rule_string_value: stringValue,
              rule_time_period: this.selectTimePeriod,
              rule_condition2: this.selectedCondition2
            },
          ];
        } else {
          selectedValuesArray = [
            {
              rule_type: "OR",
              rule_name: rulename_string,
              rule_name_key: this.selectedRuleNameKey,
              rule_condition_key: this.selectedRuleCondiKey,
              rule_condition: this.selectedCondition,
              rule_operation: this.selectedOperation,
              rule_operation_key: selecteOpeKey,
              rule_string_value: stringValue,
              rule_time_period: this.selectTimePeriod,
              rule_condition2: this.selectedCondition2
            },
          ];
        }
        this.$emit("selectionAndChanged", {
          and_con_index: this.and_index,
          and_or_index: this.and_or_index,
          rule_id: this.rule_id,
          values: selectedValuesArray,
          validation: this.validation
        });
      }
    },
    /*UPDATE VALUE IN INPUT FIELDS */
    updateOrRule() {
      let getOrConditions = this.andorConditions;
      if (this.andorConditions.rule_name && this.segmentRules && getOrConditions) {
        const matchingRule = findMatchingRule(
          this.segmentRules,
          getOrConditions
        );
        if (matchingRule) {
          let splitCondition = getOrConditions.rule_condition.split(":")[1];
          let setcondition;
          let setoperation;
          let splitcondiRule;
          let setTimePeriod;
          this.selectedRuleName = matchingRule.rule_name[0];
          const rule_key = getOrConditions.rule_key;
          if (rule_key == 'loyalty.program_tier_id') {
            this.showCol = true;
            this.loyaltyTiers = matchingRule.rule_operation
            this.selectedOperation = this.loyaltyTiers[0]
            this.selectedOperation = this.selectedTier
          }
          else if (rule_key == 'ce.event_type') {
            if (splitCondition == 'is not in' && getOrConditions.rule_value == "('Read', 'Clicked')") {
              setoperation = 'Opened';
              setTimePeriod = 'No';

            }
            else if (splitCondition == 'is in' && getOrConditions.rule_value == "('Read', 'Clicked')") {
              setoperation = 'Opened'
              setTimePeriod = 'Yes'
            }
            else if (splitCondition == 'is not' && getOrConditions.rule_value == "'Delivered'") {
              setoperation = 'Received'
              setTimePeriod = 'No'
            }
            else if (splitCondition == 'is' && getOrConditions.rule_value == "'Delivered'") {
              setoperation = 'Received'
              setTimePeriod = 'Yes'

            }
            else if (splitCondition == 'is' && getOrConditions.rule_value == "'Clicked'") {
              setoperation = 'Clicked'
              setTimePeriod = 'Yes'

            }
            else if (splitCondition == 'is not' && getOrConditions.rule_value == "'Clicked'") {
              setoperation = 'Clicked';
              setTimePeriod = 'No';
            }
            this.selectedOperation = setoperation;
            this.selectTimePeriod = setTimePeriod
          }
          else if (rule_key == 'cd.status') {
            if (splitCondition == 'is in redeem') {
              setcondition = 'Redeemed on'
              setoperation = 'any date'
            }
            if (splitCondition == 'is not in redeem') {
              setcondition = 'Redeemed on'
              setoperation = 'never'
            }
            if (splitCondition == 'is value') {
              setcondition = 'Issued on'
              setoperation = 'any date'
            }
            if (splitCondition == 'is not in active') {
              setcondition = 'Issued on'
              setoperation = 'never'
            }
            this.selectedCondition = setcondition;
            this.selectedOperation = setoperation
          }
          else if (rule_key.includes('c.udf')) {
            this.showCol = true;
            if (matchingRule.rule_condition && this.udfList && this.andorConditions.rule_value) {
              this.udfList = matchingRule.rule_condition
              this.selectedCondition = this.selectedUdf
              this.selectedOperation = getComparisonString(splitCondition);
              this.inputStringValue = this.andorConditions.rule_value;
            }
            return
          }
          else if (rule_key && rule_key.includes('sal.udf') || rule_key && rule_key.includes('sku.udf')) {
            this.showCol = true;
            if (matchingRule.rule_condition && this.purchaseudfList && this.andorConditions.rule_value) {
              this.purchaseudfList = matchingRule.rule_condition
              this.selectedCondition = this.andorConditions.rule_name
              this.selectedOperation = getComparisonString(splitCondition);
              this.inputStringValue = this.andorConditions.rule_value;
            }
            return
          }
          else {
            const condition_option = mapCondition(splitCondition, rule_key);
            splitcondiRule = condition_option.split("|");
            setcondition = splitcondiRule[0];
            setoperation = splitcondiRule[1];
          }
          if (this.rule_id == 4 && rule_key != 'cd.status') {
            this.selectedCondition = splitcondiRule[0]
            this.selectedOperation = splitcondiRule[1]
            this.selectTimePeriod = splitcondiRule[2]

          }
          else {
            if (this.rule_id == 2) {
              this.selectTimePeriod = setTimePeriod;
            } else {
              if (rule_key != 'loyalty.program_tier_id') {
                this.selectedCondition = setcondition;
                this.selectedOperation = setoperation;
                this.selectTimePeriod = setTimePeriod
              }
            }
          }
          this.inputStringValue = this.andorConditions.rule_value;
          this.inputDateOneValue = this.andorConditions.rule_value;
          this.inputNumOneValue = this.andorConditions.rule_value;

          if (this.andorConditions.rule_ex_1) {
            this.inputDateTwoValue = this.andorConditions.rule_ex_1;
            this.inputNumTwoValue = this.andorConditions.rule_ex_1;
          }
          if (
            splitCondition == "withinlast_days" ||
            splitCondition == "notwithinlast_days" ||
            splitCondition == "iswithinnext_days"
          ) {
            this.inputNumThreeValue = this.andorConditions.rule_value;
            this.selectTimePeriod = "days";
          } else if (
            splitCondition == "withinlast_weeks" ||
            splitCondition == "notwithinlast_weeks" ||
            splitCondition == "iswithinnext_weeks"
          ) {
            this.inputNumThreeValue = this.andorConditions.rule_value / 7;
            this.selectTimePeriod = "weeks";
          } else if (
            splitCondition == "withinlast_months" ||
            splitCondition == "notwithinlast_months" ||
            splitCondition == "iswithinnext_months"
          ) {
            this.inputNumThreeValue = this.andorConditions.rule_value / 30;
            this.selectTimePeriod = "months";
          }
          if (splitCondition == "date_diff_equal_to") {
            this.inputNumOneValue = "";
            this.inputNumTwoValue = this.andorConditions.rule_value;
          }
          const rule = this.andorConditions.rule_name;
          if (rule == "Email Id" && splitCondition == "none") {
            this.selectedCondition = "is empty";
          } else if (
            rule == "Gender" ||
            rule == "Contact Source" ||
            rule == "Membership Status"
          ) {
            this.selectedOperation = mapOption(this.andorConditions.rule_value);
          }
          if (rule_key == "aggr_avg") {
            this.selectedCondition = "Average of all";
          } else if (
            [
              "loyalty.total_loyalty_earned",
              "loyalty.total_giftcard_amount",
              "loyalty.loyalty_balance",
              "loyalty.giftcard_balance",
              "date(cd.issued_on)", "date(cd.redeemed_on)", "date(cd.expired_on)"
            ].includes(rule_key)
          ) {
            this.selectTimePeriod = splitcondiRule[2];
          }
        }
      }
      console.log('calling')
    },

    validate(value) {
      return !!(value != undefined && value != null && value != 'undefined' && value != 'null' && value != '' && value != []);
    },
    getDisplayText(item) {
      if (typeof item.value == 'object' || item.value == null) {
        return item.value ? "Object Value" : "Null Value";
      } else {
        return item.value;
      }
    },
    toggleSelectAll() {
      if (!this.selectAll) {
        this.selectedCampaigns = this.campaignIds.map(item => item);
      } else {
        this.selectedCampaigns = [];
      }
      this.selectAll = !this.selectAll;
    },
    updateValidation() {
      const final = this.validation;
    },
    updateCampaigns() {
      setTimeout(() => {
        this.updateOrRule();
      }, 4000);
    },
  },
  computed: {
    /*RULES NAME */
    ruleNames() {
      if (this.segmentRules && this.segmentRules.length > 0) {
        this.updateOrRule();
        const filteredRules = this.segmentRules.filter(rule => rule.rule_name[0]);

        return filteredRules.map((r) => r.rule_name);
      } else {
        return [];
      }
    },
    /*RULES CONDITION */
    getRuleConditions() {
      if (this.segmentRules && this.segmentRules.length > 0 && this.selectedRuleName) {
        const selecteCondi = this.segmentRules.find(
          (r) => r.rule_name === this.selectedRuleName
        );

        if (selecteCondi) {
          return selecteCondi.rule_condition;
        } else if (this.selectedRuleName) {
          const matchingRule = this.segmentRules.find((rule) =>
            rule.rule_name.includes(this.selectedRuleName)
          );
          if (matchingRule) {
            return matchingRule.rule_condition;
          }
        }
        return [];
      } else {
        return [];
      }
    },
    /*RULES OPERTAION */
    getRuleOperation() {
      if (this.segmentRules && this.segmentRules.length > 0) {
        const selectedRule = this.segmentRules.find(
          (r) => r.rule_name === this.selectedRuleName
        );
        if (selectedRule) {
          if (this.selectedRuleName == 'Loyalty Tier') {
            return selectedRule.rule_operation
          }
          const selectedRuleName = this.selectedRuleName[0];
          const selectedCondition = this.selectedCondition;
          return this.handleArray(
            selectedRule,
            selectedCondition,
            selectedRuleName
          );
        } else if (this.selectedRuleName) {
          const matchRule = findMatchingRule(
            this.segmentRules,
            this.andorConditions
          );
          return this.handleArray(
            matchRule,
            this.selectedCondition,
            this.selectedRuleName
          );
        }
      } else {
        return [];
      }
    },
    /*RULES TYPE TEXT< SELCTION DATE INPUT */
    getRuleType() {
      if (this.segmentRules && this.segmentRules.length > 0) {
        const selectedRule = this.segmentRules.find(
          (r) => r.rule_name === this.selectedRuleName
        );
        const selectedCond = this.selectedCondition;

        if (
          selectedCond === "within last" ||
          selectedCond === "not within last"
        ) {
          if (selectedRule) {
            return selectedRule.rule_type;
          } else if (this.selectedRuleName) {
            const matchRule = findMatchingRule(
              this.segmentRules,
              this.andorConditions
            );
            return matchRule.rule_type;
          }
          return selectedRule ? selectedRule.rule_type : [];
        } else if (['within last', 'not within last', 'is in next'].includes(this.selectedOperation)) {
          return ['days', 'weeks', 'months'];
        } else if (this.selectedOperation == 'is (in range)') {
          return ['Last range of days'];
        } else if (
          [
            "loyalty.total_loyalty_earned",
            "loyalty.total_giftcard_amount",
            "loyalty.loyalty_balance",
            "loyalty.giftcard_balance",
            "date(cd.issued_on)",
            "date(cd.redeemed_on)",
            "date(cd.expired_on)"
          ].includes(this.selectedRuleNameKey)
        ) {
          if (selectedRule) {
            return selectedRule.rule_type;
          } else if (this.selectedRuleName) {
            const matchRule = findMatchingRule(
              this.segmentRules,
              this.andorConditions
            );
            return matchRule.rule_type;
          }
        } else if (['Received', 'Opened', 'Clicked'].includes(this.selectedOperation)) {
          return ['Yes', 'No'];
        }
        else {
          return [];
        }
      } else {
        return [];
      }
    },
    /*SHOW / HIDE OPERATION SELECTION BOX*/
    shouldShowOperationInput() {
      if (this.selectedRuleName == 'Interaction-Rules' || this.selectedRuleName == 'Loyalty Tier' || this.selectedRuleName == 'Others') {
        return true;
      } else {
        return (
          this.selectedOperation === null ||
          [
            "is",
            "is not",
            "is(in range)",
            "Total",
            "Balance",
            "Average of all",
            "is(year ignored)",
            "Issued on",
            "Redeemed on",
            "Expiry on"
          ].includes(this.selectedCondition)
        );
      }
    },

    /*SHOW / HIDE INPUT NUMBER ON WITHIN AND NOT WITHIN SELECTION BOX*/
    shouldShowNumberInputThree() {
      if (this.selectedRuleName == 'Discount-code Date') {
        return (
          this.selectedCondition != "" &&
          ([
            "withinlast_days", "notwithinlast_days", "iswithinnext_days"
          ].includes(this.selectedOperation) ||
            ["number_with"].includes(this.selecteOpeKey))
        );
      } else {
        return (
          this.selectedCondition !== "" &&
          ([
            "within last",
            "not within last",
            "days since last purchase",
          ].includes(this.selectedCondition) ||
            ["number_with"].includes(this.selecteOpeKey))
        );
      }
    },
    /*SHOW / HIDE INPUT TEXT*/
    shouldShowStringValueInput() {
      return (
        this.selectedRuleName == null ||
        ["text"].includes(this.selecteOpeKey) ||
        [
          "contains",
          "doesn't contain",
          "Equals",
          "Doesn't equal",
          "One of",
          "Contains",
          "Doesn't Contain",
          "Only like",
        ].includes(this.selectedCondition)
      );
    },
    /*SHOW / HIDE INPUT DATE*/
    shouldShowDateOneInput() {
      return (
        this.selectedOperation != null &&
        (["date", "date_range"].includes(this.selecteOpeKey) ||
          ["After", "Before"].includes(this.selectedOperation) ||//Puchase Date
          (["Issued on", "Redeemed on", "Expiry on"].includes(this.selectedCondition) && ["is"].includes(this.selectedOperation)
            && ["Equal to", "Between", "On or after", "On or before", "After", "Before"].includes(this.selectTimePeriod))
        )
      );
    },
    /*SHOW / HIDE INPUT DATE IN RANGE*/
    shouldShowDateTwoInput() {
      return (
        this.selectedOperation != null &&
        ["date_range"].includes(this.selecteOpeKey) ||
        (["Issued on", "Redeemed on", "Expiry on"].includes(this.selectedCondition) && ["is"].includes(this.selectedOperation)
          && ["Between"].includes(this.selectTimePeriod))
      );
    },
    /*SHOW / HIDE INPUT SELECTION IF DAY/WEEK/YEAR*/
    shouldShowTimePeriodSelect() {
      if (this.selectedRuleName == 'Interaction-Rules') {
        return true
      } else {
        return this.selectedCondition !== null && this.getRuleType.length !== 0;
      }
    },
    /*SHOW / HIDE INPUT NUMBER IN RANGE*/
    shouldShowNumOneInput() {
      return (
        this.selectedCondition !== null &&
        ["number", "number_range"].includes(this.selecteOpeKey) &&
        !["Empty"].includes(this.selectTimePeriod)
      );
    },
    /*SHOW / HIDE INPUT NUMBER IN RANGE*/
    shouldShowNumTwoInput() {
      return (
        this.selectedCondition !== null &&
        (["number_range"].includes(this.selecteOpeKey) ||
          ["days since last purchase"].includes(this.selectedCondition) ||
          (["Between"].includes(this.selectTimePeriod) && ["Points", "Amount"].includes(this.selectedOperation)))
      );
    },
    inputNumThreeColor() {
      return parseFloat(this.inputNumThreeValue) >= 0 && this.inputNumThreeValue != '' ? '' : 'red';
    },
    inputNumTwoColor() {
      return parseFloat(this.inputNumTwoValue) >= 0 && this.inputNumTwoValue != '' ? '#7367F0' : 'red';
    },
    inputNumOneColor() {
      return parseFloat(this.inputNumOneValue) >= 0 && this.inputNumOneValue != '' ? '#7367F0' : 'red';
    },
    inputTextColor() {
      return (this.inputStringValue) != '' ? '' : 'red'
    },
    validation() {
      if (this.selectedRuleName == 'Interaction-Rules') {
        if (this.selectedCondition != '' && this.selectTimePeriod != null) {
          return 'success';
        } else {
          return 'error';
        }
      } else if (this.selectedRuleName == 'Discount-code Date') {
        if (this.selectedCondition != '' && this.selectTimePeriod != null && this.selectedOperation != null && this.selectedCondition2 != null) {
          return 'success';
        } else {
          return 'error';
        }
      }
      else {
        if (this.and_or_index <= this.andOrLength - 2) {
          if ((this.shouldShowNumberInputThree && this.inputNumThreeColor == 'red') ||
            (this.shouldShowStringValueInput && this.inputTextColor == 'red') ||
            (this.shouldShowNumOneInput && this.inputNumOneColor == 'red') ||
            (this.shouldShowNumTwoInput && this.inputNumTwoColor == 'red') || this.selectedRuleName == '') {
            return 'error';
          } else {
            return 'success';
          }
        }
        // else {
        //   return 'not';
        // }
      }
    },

    getDynamicColsInput() {
      const trueCount = [
        this.shouldShowStringValueInput,
        this.shouldShowDateOneInput,
        this.shouldShowDateTwoInput,
        this.shouldShowTimePeriodSelect,
        this.shouldShowNumOneInput,
        this.shouldShowNumTwoInput
      ].filter(Boolean).length;

      if (trueCount === 1) {
        return 3;
      } else if (trueCount === 2) {
        return 5;
      } else if (trueCount > 2) {
        return 6;
      } else {
        return 4; // or any default value if none is true
      }
    },
    getDynamicColsOPeration() {
      const trueCount = [
        this.shouldShowOperationInput,
        this.shouldShowNumberInputThree
      ].filter(Boolean).length;

      if (trueCount === 1) {
        return 2;
      } else if (trueCount === 2) {
        return 3;
      }
    },
  },
  watch: {
    /*SET DEFAULT RULE CONDITION ON RULE NAME AND GET RULE NAMEW*/
    selectedRuleName() {
      if (Array.isArray(this.selectedRuleName)) {
        if (this.selectedRuleName != 'Discount-code Date' && this.selectedRuleName != 'Interaction-Rules') {
          if (this.selectedRuleName == 'Loyalty Tier') {
            this.loyaltyTiers = this.getRuleOperation
            this.emitSelection();
          }
          this.selectedCondition = this.getRuleConditions[0];
          this.selectedOperation = this.getRuleOperation[0];
          this.selectTimePeriod = this.getRuleType[0];
          this.inputStringValue = "";
          this.inputDateOneValue = "";
          this.inputDateTwoValue = "";
          this.inputNumOneValue = "";
          this.inputNumTwoValue = "";
          this.inputNumThreeValue = "";
        }
      }
      else {
        //  this.updateOrRule()
      }
      //this.updateOrRule()
      this.updateValidation()
      this.emitSelection();
    },

    selectedCondition2() {
      this.showCondition = true
      //  this.selectedCondition = this.getRuleConditions[0];
      //  this.selectedOperation = this.getRuleOperation[0];
      this.selectTimePeriod = this.getRuleType[0];
      this.inputStringValue = "";
      this.inputDateOneValue = "";
      this.inputDateTwoValue = "";
      this.inputNumOneValue = "";
      this.inputNumTwoValue = "";
      this.inputNumThreeValue = "";
      this.updateValidation()
      this.emitSelection();
    },
    /*SET DEFAULT RULE OPERATION ON RULE NAME GET CONDITON VALUE*/
    selectedCondition(newCondition, oldCondition) {

      this.selectedOperation = this.getRuleOperation[0];
      this.selectTimePeriod = this.getRuleType[0]; //For Time period selection
      this.inputStringValue = "";
      this.inputDateOneValue = "";
      this.inputDateTwoValue = "";
      this.inputNumOneValue = "";
      this.inputNumTwoValue = "";
      this.inputNumThreeValue = "";
      this.updateValidation()
      this.emitSelection();
    },
    /* GET OPERATION VALUE*/
    selectedOperation(newOperation, oldOperation) {
      if (oldOperation && oldOperation != newOperation) {
        if (newOperation == 'any date' || newOperation == 'never') {
          this.showInputs = false
          this.selectTimePeriod = null
        }
        else {
          this.showInputs = true
        }
        this.selectTimePeriod = this.getRuleType[0];
        // this.inputStringValue = "";
        this.inputDateOneValue = "";
        this.inputDateTwoValue = "";
        this.inputNumOneValue = "";
        this.inputNumTwoValue = "";
        this.inputNumThreeValue = "";
        this.updateValidation()
        this.emitSelection();
      }
    },
    /* GET INPUT STRING VALUE*/
    inputStringValue() {
      this.updateValidation();
      this.emitSelection();
    },
    /* GET DATE ONE VALUE*/
    inputDateOneValue() {
      this.updateValidation();
      this.emitSelection();
    },
    /* GET DATE TWO VALUE*/
    inputDateTwoValue() {
      this.updateValidation();
      this.emitSelection();
    },
    /* GET Num ONE VALUE*/
    inputNumOneValue() {
      this.updateValidation();
      this.emitSelection();
    },
    /* GET Num TWO VALUE*/
    inputNumTwoValue() {
      this.updateValidation();
      this.emitSelection();
    },
    /* GET Num THREE VALUE*/
    inputNumThreeValue() {
      this.updateValidation();
      this.emitSelection();
    },
    /* GET SELECTION TIME PERIOD VALUE*/
    selectTimePeriod(newTime, oldTime) {
      if (!newTime) {
        this.selectTimePeriod = this.getRuleType[0];
      }
      this.updateValidation()
      this.emitSelection();
    },
    code: {
      immediate: true,
      handler(newVal) {
        this.selectedCondition2 = newVal;
        //  this.updateOrRule()
      }
    },

    campaignNames: {
      immediate: true,
      handler(newVal) {
        if (Array.isArray(newVal) && newVal.length > 0) {
          this.campaignsList = newVal;
          //  this.updateOrRule()
        } else {
          this.campaignsList = [];
        }
      },
      deep: true
    },
    couponCodes: {
      immediate: true,
      handler(newVal) {
        if (Array.isArray(newVal) && newVal.length > 0) {
          this.discountCodes = newVal;
        } else {
          this.discountCodes = [];
        }
      },
      deep: true
    },

    selectedCampaigns() {
      this.updateOrRule()
      this.emitSelection();
    },
    isOrConditionFilled: {
      immediate: true,
      handler(newVal) {
        this.$emit('orConditionFilledChanged', newVal);
      }
    },
    andOrLength: {
      immediate: true,
      handler() {
        this.updateValidation();
      }
    },
    inputNumThreeColor: {
      handler: function () {
        this.updateValidation();
      }
    },
    inputNumTwoColor: {
      handler: function () {
        this.updateValidation();
      }
    },
    inputNumOneColor: {
      handler: function () {
        this.updateValidation();
      }
    },
    inputTextColor: {
      handler: function () {
        this.updateValidation();
      }
    },
    udfs: {
      immediate: true,
      handler(newVal) {
        this.udfList = newVal
      },
      deep: true
    },
    purchaseudfs: {
      immediate: true,
      handler(newVal) {
        this.purchaseudfList = newVal
      },
      deep: true
    },
  },
  mounted() {
    if (this.ruleNames.length > 0 && this.ruleNames[0] == 'Discount-code Date') {
      this.selectedRuleName = this.ruleNames[0];
    }
    else if (this.ruleNames.length > 0 && this.ruleNames[0] == 'Interaction-Rules') {
      this.campaignIds = JSON.parse(localStorage.getItem('campaignId'));
      let temp = this.andorConditions.rule_name ? this.andorConditions.rule_name.split(',') : null
      if (temp != null) {
        this.selectedCampaigns = temp.map(key => {
          return this.campaignIds.find(campaign => campaign.key == key);
        }).filter(campaign => campaign !== undefined);
      }
      if (this.$route.params.id != 'new') {
        this.selectedRuleName = this.ruleNames[0];
        this.selectedOperation = this.getRuleOperation[0];
        this.selectTimePeriod = this.getRuleType[0]
      }
      else {
        this.selectedRuleName = this.ruleNames[0];
        this.selectedOperation = this.getRuleOperation[0];
        this.selectTimePeriod = this.getRuleType[0]
      }

    }
    else if (this.selectedRuleName == 'Loyalty Tier' && this.$route.params.id == 'new') {
      this.loyaltyTiers = this.getRuleOperation
    }
    this.allUDf = JSON.parse(localStorage.getItem('customer-udfs'))
    this.purchaseUDFall = JSON.parse(localStorage.getItem('purchase-udfs'))
    if (this.selectedRuleName && this.selectedCondition && this.selectedOperation) {
      this.updateCampaigns()
    }
    // this.emitSelection()
  },

};
/*RETUN SELCTED RULE KEY */
function findMatchingRule(segmentRules, matchObj) {
  const isUDFRule = matchObj.rule_key?.includes('c.udf');
  const purchaseUDFRule = matchObj.rule_key?.includes('sal.udf') || matchObj.rule_key?.includes('sku.udf');
  const matchingRule = segmentRules.find((rule) => {
    if (isUDFRule) {
      return rule.rule_name_string?.includes('C.UDF');
    } else if (purchaseUDFRule) {
      return rule.rule_name_key?.includes('sal.udf') || rule.rule_name_key?.includes('sku.udf');
    }
    return rule.rule_name_key?.includes(matchObj.rule_key);
  });

  return matchingRule;
}
/* RETURN CONDITION OPERATION AND VALUE FOR SELECTION WHILE UPDATE STRING*/
function mapCondition(inputCondition, s_rule_key) {
  let conditionMap = {
    "is": "is|Equal to",
    "is in": "is|One of",
    "is not": "is not|Equal to",
    "is not in": "is not|One of",
    "none": "is|Empty",
    "isToday": "is|Today",
    "between": "is|Between",
    "onOrAfter": "is|On or after",
    "onOrBefore": "is|On or before",
    "after": "is|After",
    "before": "is|Before",
    "range_between": "is(in range)|Last range of days",
    "withinlast_days": "within last",
    "withinlast_weeks": "within last",
    "withinlast_months": "within last",
    "notwithinlast_days": "within last",
    "notwithinlast_weeks": "within last",
    "notwithinlast_months": "within last",
    "does not contain": "doesn't contain",
    "=": "is|Equal to",
    "!=": "is not|Equal to",
    "equal": "is",
    "equals": "is",
    "equal to": "is",
    "is equal": "is",
    "before_between": "is(year ignored)|Last range of days",
    "after_between": "is(year ignored)|Next Range Of Days",
    "ignoreYear_is": "is(year ignored)|Equal to",
    "ignoreYear_between": "is(year ignored)|Between",
    "ignoreYear_onOrAfter": "is(year ignored)|On or after",
    "ignoreYear_onOrBefore": "is(year ignored)|On or before",
    "ignoreYear_after": "is(year ignored)|After",
    "ignoreYear_before": "is(year ignored)|Before",
    "notafter": "is not|After",
    "notbefore": "is not|Before",
    "date_diff_equal_to": "days since last purchase",
    ">": "Total|is more than",
    "<": "Total|is less than",
    "is value": "Issued",
    "is in redeem": "Redeemed",
    "is not in redeem": "Not Redeemed",
    "isetoday": "Expiry on|is|Today",
    "isequal": "Expiry on|is|Equal to",
    "isebetween": "Expiry on|is|Between",
    "iseonOrAfter": "Expiry on|is|On or after",
    "iseonOrBefore": "Expiry on|is|On or before",
    "iseafter": "Expiry on|is|After",
    "isebefore": "Expiry on|is|Before",
    "iswithinnext_days": "Expiry on|is in next|days",
    "iswithinnext_weeks": "Expiry on|is in next|weeks",
    "iswithinnext_months": "Expiry on|is in next|months"
  };

  if (s_rule_key == "c.home_store" && inputCondition == "is not") {
    conditionMap = {
      "is not": "is|Doesn't equal",
    };
  } else if (
    ["aggr_tot", "aggr_avg", "aggr_count"].includes(s_rule_key) &&
    inputCondition == "between"
  ) {
    conditionMap = {
      between: "Total|is in the range of",
    };
  } else if (
    [
      "sal.store_number",
      "sal.subsidiary_number",
      "sal.sku",
      "sku.item_category",
      "sku.department_code",
      "sku.description",
      "sku.class_code",
      "sku.subclass_code",
      "sku.dcs",
      "sku.vendor_code",
    ].includes(s_rule_key)
  ) {
    conditionMap = {
      is: "Equals",
      "is not": "Doesn't equal",
      "is in": "One of",
      contains: "Contains",
      "does not contain": "Doesn't Contain",
      "only like": "Only like",
    };
  } else if (["loyalty.total_loyalty_earned"].includes(s_rule_key)) {
    conditionMap = {
      ">": "Total|Points|More than",
      "<": "Total|Points|Less than",
      "=": "Total|Points|Equal to",
    };
  } else if (["loyalty.total_giftcard_amount"].includes(s_rule_key)) {
    conditionMap = {
      ">": "Total|Amount|More than",
      "<": "Total|Amount|Less than",
      "=": "Total|Amount|Equal to",
      between: "Total|Amount|Between",
      none: "Total|Amount|Empty",
    };
  } else if (["loyalty.loyalty_balance"].includes(s_rule_key)) {
    conditionMap = {
      ">": "Balance|Points|More than",
      "<": "Balance|Points|Less than",
      "=": "Balance|Points|Equal to",
      between: "Balance|Points|Between",
      none: "Balance|Points|Empty",
    };
  } else if (["loyalty.giftcard_balance"].includes(s_rule_key)) {
    conditionMap = {
      ">": "Balance|Amount|More than",
      "<": "Balance|Amount|Less than",
      "=": "Balance|Amount|Equal to",
      between: "Balance|Amount|Between",
      none: "Balance|Amount|Empty",
    };
  } else if (["date(cd.issued_on)", "date(cd.redeemed_on)"].includes(s_rule_key)) {
    let condi_name = ""
    if (s_rule_key == "date(cd.issued_on)") {
      condi_name = "Issued on"
    } else if (s_rule_key == "date(cd.redeemed_on)") {
      condi_name = "Redeemed on"
    }
    conditionMap = {
      "isToday": `${condi_name}|is|Today`,
      "equal to": `${condi_name}|is|Equal to`,
      "between": `${condi_name}|is|Between`,
      "onOrAfter": `${condi_name}|is|On or after`,
      "onOrBefore": `${condi_name}|is|On or before`,
      "after": `${condi_name}|is|After`,
      "before": `${condi_name}|is|Before`,
      "withinlast_days": `${condi_name}|within last|days`,
      "withinlast_weeks": `${condi_name}|within last|weeks`,
      "withinlast_months": `${condi_name}|within last|months`,
      "notwithinlast_days": `${condi_name}|not within last|days`,
      "notwithinlast_weeks": `${condi_name}|not within last|weeks`,
      "notwithinlast_months": `${condi_name}|not within last|months`,
      "range_between": `${condi_name}|is (in range)|Last range of days`,
      "Issued on": `${condi_name}|is|Today`,
      "Redeemed on": `${condi_name}|is|Today`,
      "Expiry on": `${condi_name}|is|Today`,
    }
  }
  return conditionMap[inputCondition] || inputCondition;
}
/* RETURN  OPERATION WHILE UPDATE STRING*/
function mapOption(inputCondition) {
  const optionMap = {
    AddedManually: "Manual Upload",
    WebForm: "Web Form",
    "Mobile Opt-In": "Mobile Opt-in",
    eComm: "eCommerce",
  };
  return optionMap[inputCondition] || inputCondition;
}
function getComparisonString(comparisonType) {
  switch (comparisonType) {
    case "is":
      return 'Equal to';
    case "is not":
      return "Doesn't equal";
    case "is in":
      return 'One of';
    case "contains":
      return 'contains';
    case "does not contain":
      return "doesn't contain";
    case "starts with":
      return 'Starts with';
    case "ends with":
      return 'Ends with';
    case "is empty":
      return 'Is empty';
    default:
      return 'Invalid comparison type';
  }
}

</script>
<template>

  <v-row class="align-center">
    <v-snackbar v-model="showError" :timeout="timeout" color="red-darken-1">
      {{ text }}

      <template v-slot:actions>
        <v-btn color="black" variant="text" @click="showError = false">
          Close
        </v-btn>
      </template>
    </v-snackbar>
    <v-col cols="12" md="1"
      v-if="and_or_index !== 0 && selectedRuleName != 'Discount-code Date' && selectedRuleName != 'Interaction-Rules'"
      class="d-flex justify-center">
      <v-chip @click="addAndOrCondition" size="small" variant="outlined" style="border: 1px solid #7367f0"
        class="px-4 py-3 rule-or-btn justify-center" label>
        <span class="text-indigo-darken-1 text-subtitle-2">OR</span>
      </v-chip>
    </v-col>
    <v-col cols="12" md="4"
      :class="{ 'last-or-condition': isLastAndOrCondition && selectedRuleName != 'Discount-code Date' && selectedRuleName != 'Interaction-Rules' }">
      <div v-if="selectedRuleName != 'Discount-code Date' && selectedRuleName != 'Interaction-Rules'"
        class="d-flex gap-2">
        <AppSelect placeholder="Rules" class="rule-border text-ellipsis" :items="ruleNames"
          v-model="selectedRuleName" />
        <AppSelect placeholder="is" class="rule-border text-ellipsis" :items="udfList" v-model="selectedCondition"
          v-if="selectedRuleName == 'Others' && andorConditions.panel_id == 1" />
        <AppSelect placeholder="is" class="rule-border text-ellipsis" :items="purchaseudfList"
          v-model="selectedCondition" v-else-if="selectedRuleName == 'Others' && andorConditions.panel_id == 3" />

        <AppSelect placeholder="is" class="rule-border text-ellipsis" :items="getRuleConditions"
          v-model="selectedCondition" v-else-if="selectedRuleName != 'Others'" />
      </div>
      <div v-else class="d-flex gap-2">
        <AppSelect placeholder="Rules" class="rule-border text-ellipsis " :items="ruleNames" v-model="selectedRuleName"
          v-show="false" />
        <AppSelect placeholder="Discount Name" class="rule-border text-ellipsis" :items="discountCodes"
          v-model="selectedCondition2" :class="validate(selectedCondition2) ? '' : 'rule-border-red'"
          v-if="selectedRuleName == 'Discount-code Date'" />
        <AppSelect placeholder="Operation" class="rule-border text-ellipsis" :items="getRuleConditions"
          v-model="selectedCondition" :class="validate(selectedCondition) ? '' : 'rule-border-red'"
          v-if="showCondition && selectedRuleName == 'Discount-code Date'" />
        <v-select v-model="selectedCampaigns" :items="campaignIds" item-title="value" item-value="key" item-text="value"
          return-object multiple placeholder="Select Campaign" class="rule-border text-ellipsis"
          :class="validate(selectedCampaigns) ? '' : 'rule-border-red'"
          :color="validate(selectedCampaigns) ? '' : 'red'"
          v-if="showCondition && selectedRuleName == 'Interaction-Rules'" style="max-height: 50px; overflow-y: auto;">
          <template v-slot:prepend-item>
            <div class="ma-2 ml-6" @click="toggleSelectAll" style="cursor: pointer;">
              <v-icon :icon="selectAll ? 'tabler-square-check-filled' : 'tabler-square'" size="22" color="primary"
                class="mr-2"></v-icon>
              <span class="font-weight-bold mt-2">Select All</span>
            </div>
          </template>
        </v-select>
      </div>

    </v-col>
    <v-col cols="12" :md="getDynamicColsOPeration"
      :class="{ 'last-or-condition': isLastAndOrCondition && selectedRuleName != 'Discount-code Date' && selectedRuleName != 'Interaction-Rules' }"
      class="d-flex gap-2" v-if="showCol">
      <AppSelect placeholder="operation" class="rule-border text-ellipsis w-120" :items="getRuleOperation"
        v-model="selectedOperation" v-if="shouldShowOperationInput && selectedRuleName != 'Loyalty Tier'" />
      <AppSelect placeholder="operation" class="rule-border text-ellipsis" :items="loyaltyTiers"
        v-model="selectedOperation" v-else-if="shouldShowOperationInput && selectedRuleName == 'Loyalty Tier'" />
      <v-text-field placeholder="Value 3" class="w-50" type="number" v-if="shouldShowNumberInputThree && showInputs"
        v-model="inputNumThreeValue" min="0" :color="inputNumThreeColor"
        :class="inputNumThreeColor === 'red' ? 'rule-border-red' : 'rule-border'" />
    </v-col>
    <v-col cols="12" :md="getDynamicColsInput" class="d-flex align-center gap-2"
      :class="{ 'last-or-condition': isLastAndOrCondition && selectedRuleName != 'Discount-code Date' && selectedRuleName != 'Interaction-Rules' }">
      <v-text-field placeholder="Value" type="text" class="" v-if="shouldShowStringValueInput && showInputs"
        v-model="inputStringValue" :class="inputTextColor == 'red' ? 'rule-border-red' : 'rule-border'"
        :color="inputTextColor" />

      <AppSelect class="rule-border text-ellipsis w-100" :items="getRuleType"
        v-if="shouldShowTimePeriodSelect && showInputs" v-model="selectTimePeriod"
        :class="validate(selectTimePeriod) ? '' : 'rule-border-red'" />
      <AppDateTimePicker clearable type="text" class="ma-2" width="100px" placeholder="YYYY-MM-DD"
        v-if="shouldShowDateOneInput && showInputs" v-model="inputDateOneValue"
        :class="{ 'rule-border': inputDateOneValue !== '', 'rule-border-red': inputDateOneValue === '' }" />
      <AppDateTimePicker clearable type="text" class=" ma-2" width="100px" placeholder="YYYY-MM-DD"
        v-if="shouldShowDateTwoInput && showInputs" v-model="inputDateTwoValue"
        :class="validate(inputDateTwoValue) ? 'rule-border' : 'rule-border-red'" />
      <v-text-field placeholder="Value" class="w-25" type="number" v-if="shouldShowNumOneInput && showInputs"
        v-model="inputNumOneValue" min="0" :color="inputNumOneColor"
        :class="inputNumOneColor === 'red' ? 'rule-border-red' : 'rule-border'" />
      <v-text-field placeholder="value" class="w-25" type="number" v-if="shouldShowNumTwoInput && showInputs"
        v-model="inputNumTwoValue" min="0" :color="inputNumTwoColor"
        :class="inputNumTwoColor === 'red' ? 'rule-border-red' : 'rule-border'" />
      <div
        v-if="and_or_index != 0 && selectedRuleName != 'Discount-code Date' && selectedRuleName != 'Interaction-Rules'">
        <v-icon @click="onRemove" icon="tabler-trash" color="#7367F0" size="24"></v-icon>
      </div>
      <div v-else>

        <v-btn color="error"
          v-if="andOrLength < 3 && selectedRuleName != 'Discount-code Date' && selectedRuleName != 'Interaction-Rules'"
          prepend-icon="tabler-trash-x" @click="removeSet">
          Remove</v-btn>
      </div>
    </v-col>
  </v-row>
</template>
<style scoped>
.rule-bg-white {
  background-color: #e64114;
}

.rule-or-btn {
  color: rgb(115, 103, 240);
  border: 1px solid white;
}

.last-or-condition {
  opacity: 0.2;
  pointer-events: none;
}


.app-picker-field {
  width: 100%;
}

.text-ellipsis {
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.rule-border {
  border: 1px solid #7367F0;
  border-radius: 8px;
}

.rule-border-red {
  border: 1px solid red !important;
  border-radius: 8px;
}

.custom-snackbar {
  position: fixed;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
}
</style>
