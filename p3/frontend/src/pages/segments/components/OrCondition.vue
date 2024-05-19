<script>
export default {
  props: {
    isLastOrCondition: Boolean,
    isOrConditionFilled: Boolean,
    or_index: Number,
    segmentRules: Array,
    rule_id: Number,
    orConditions: Array,
    OrLength: Number,
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
      loyaltyTiers: [],
      udfList: [],
      allUDf: {},
      purchaseudfList: [],
      purchaseUDFall: {}
    };
  },
  methods: {
    /* SHOW/HIDE INPUT AS PER CONDITION AND OPERATION */
    handleArray(sRule, sCondition, sRuleName) {
      const conditionRemovals = {
        "is not": ["Empty"],
        is: ["Last range of days", "Next Range Of Days"],
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
    addOrCondition() {
      if (this.isOrConditionFilled) {
        this.$emit("addOrCondition");
      } else {
        if (this.selectedRuleName == null) {
          this.showError = true;
        }
      }
    },
    /*REMOVE OR CONDITION*/
    onRemove() {
      this.$emit("remove");
    },
    /*ON CHANGE OR CONDITION PASS TO RULES*/
    emitSelection() {
      let selecteKey = this.segmentRules.find(
        (r) => r.rule_name == this.selectedRuleName
      );
      if (!selecteKey) {
        if (this.orConditions) {
          selecteKey = findMatchingRule(this.segmentRules, this.orConditions);
        }
      } else {
        selecteKey = selecteKey;
      }
      if (selecteKey) {
        const rulename_string = selecteKey.rule_name_string[0];
        const selecteConKeyindex = selecteKey.rule_condition.indexOf(
          this.selectedCondition
        );
        if (selecteConKeyindex !== -1) {
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
        } else if (selecteOpeKey == "date") {
          stringValue = this.inputDateOneValue;
        } else if (selecteOpeKey == "date_range") {
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
          this.selectedCondition == "not within last"
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
        if (this.selectTimePeriod == "Between") {
          stringValue = '';
          if (this.inputNumOneValue && this.inputNumTwoValue) {
            stringValue = `${this.inputNumOneValue}|${this.inputNumTwoValue}`;
          }
        } else if (this.selectTimePeriod == "Empty") {
          stringValue = "IS NULL";
        } else if (this.selectedRuleName == "Loyalty Tier") {
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
          console.log(this.selectedRuleNameKey, 'condition-key',)
        }

        const selectedValuesArray = [
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
          },
        ];
        if (!(this.rule_id === 4 && this.or_index === 1 && this.rule_id === 2)) {
          this.$emit("selectionChanged", {
            or_index: this.or_index,
            rule_id: this.rule_id,
            values: selectedValuesArray,
          });
        }
      }
    },
    /*UPDATE VALUE IN INPUT FIELDS */
    updateOrRule() {
      const getOrConditions = this.orConditions;
      if (this.orConditions.rule_name) {
        const matchingRule = findMatchingRule(
          this.segmentRules,
          getOrConditions
        );
        if (matchingRule) {
          const splitCondition = getOrConditions.rule_condition.split(":")[1];
          this.selectedRuleName = matchingRule.rule_name[0];
          const rule_key = getOrConditions.rule_key;
          if (rule_key == 'loyalty.program_tier_id') {
            this.showCol = true;
            this.loyaltyTiers = matchingRule.rule_operation
            this.selectedOperation = this.loyaltyTiers[0]
            this.selectedOperation = this.selectedTier
          }
          else if (rule_key.includes('c.udf')) {
            if (matchingRule.rule_condition && this.udfList && this.orConditions.rule_value) {

              this.showCol = true;
              this.udfList = matchingRule.rule_condition
              this.selectedCondition = this.selectedUdf
              this.selectedOperation = getComparisonString(splitCondition);
              this.inputStringValue = this.orConditions.rule_value;
            }
            return
          }
          else if (rule_key.includes('sal.udf') || rule_key.includes('sku.udf')) {
            this.showCol = true;
            this.purchaseudfList = matchingRule.rule_condition
            this.selectedCondition = this.editedPurchaseUDF
            this.selectedOperation = getComparisonString(splitCondition);
            this.inputStringValue = this.orConditions.rule_value;
          }
          else {
            const condition_option = mapCondition(splitCondition, rule_key);
            const splitcondiRule = condition_option.split("|");
            const setcondition = splitcondiRule[0];
            const setoperation = splitcondiRule[1];

            this.selectedCondition = setcondition;
            this.selectedOperation = setoperation;

            this.inputStringValue = this.orConditions.rule_value;
            this.inputDateOneValue = this.orConditions.rule_value;
            this.inputNumOneValue = this.orConditions.rule_value;

            if (this.orConditions.rule_ex_1) {
              this.inputDateTwoValue = this.orConditions.rule_ex_1;
              this.inputNumTwoValue = this.orConditions.rule_ex_1;
            }
            if (
              splitCondition == "withinlast_days" ||
              splitCondition == "notwithinlast_days"
            ) {
              this.inputNumThreeValue = this.orConditions.rule_value;
              this.selectTimePeriod = "days";
            } else if (
              splitCondition == "withinlast_weeks" ||
              splitCondition == "notwithinlast_weeks"
            ) {
              this.inputNumThreeValue = this.orConditions.rule_value / 7;
              this.selectTimePeriod = "weeks";
            } else if (
              splitCondition == "withinlast_months" ||
              splitCondition == "notwithinlast_months"
            ) {
              this.inputNumThreeValue = this.orConditions.rule_value / 30;
              this.selectTimePeriod = "months";
            }
            if (splitCondition == "date_diff_equal_to") {
              this.inputNumOneValue = "";
              this.inputNumTwoValue = this.orConditions.rule_value;
            }
            const rule = this.orConditions.rule_name;
            if (rule == "Email Id" && splitCondition == "none") {
              this.selectedCondition = "is empty";
            } else if (
              rule == "Gender" ||
              rule == "Contact Source" ||
              rule == "Membership Status"
            ) {
              this.selectedOperation = mapOption(this.orConditions.rule_value);
            }
            if (rule_key == "aggr_avg") {
              this.selectedCondition = "Average of all";
            } else if (
              [
                "loyalty.total_loyalty_earned",
                "loyalty.total_giftcard_amount",
                "loyalty.loyalty_balance",
                "loyalty.giftcard_balance",
              ].includes(rule_key)
            ) {
              this.selectTimePeriod = splitcondiRule[2];
            }
          }
        }
      }
    },

    getDynamicColsOPeration() {
      if (this.selectedRuleName == 'Loyalty Enrolled Date') {
        return '3'
      } else {
        return '2'
      }
    },
    getDynamicColsInput(index) {
      if (this.selectedRuleName == 'Loyalty Enrolled Date' && index == 0) {
        return '4';
      }
      else if (this.selectedRuleName == 'Loyalty Enrolled Date' && index != 0) {
        return '3'
      }
      if (this.selectedRuleName == 'Loyalty Earned' && index == 0) {
        return '6'
      }
      else if (this.selectedRuleName == 'Loyalty Earned' && index != 0) {
        return '5'
      }
      else {
        return '5';
      }
    },
    getDynamicColsRule() {
      if (this.selectedRuleName == 'Loyalty Enrolled Date') {
        return '5';
      }
      else {
        return '4'
      }
    },
    removeSet() {
      this.$emit('removeOrSet', this.or_index)
    },


  },
  computed: {
    /*RULES NAME */
    ruleNames() {
      if (this.segmentRules && this.segmentRules.length > 0) {
        this.updateOrRule();
        const filteredRules = this.segmentRules.filter(rule => !["Discount Code Status", "Discount-code Date"].includes(rule.rule_name[0]));

        return filteredRules.map((r) => r.rule_name);
      } else {
        return [];
      }
    },
    /*RULES CONDITION */
    getRuleConditions() {
      if (this.segmentRules && this.segmentRules.length > 0 && this.selectedRuleName) {
        const selecteCondi = this.segmentRules.find(
          (r) => r.rule_name == this.selectedRuleName
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
          let selectedRuleName;
          selectedRuleName = this.selectedRuleName[0];
          if (this.selectedRuleName == 'Loyalty Tier') {
            return selectedRule.rule_operation
          }
          const selectedCondition = this.selectedCondition;
          return this.handleArray(
            selectedRule,
            selectedCondition,
            selectedRuleName
          );
        } else if (this.selectedRuleName) {
          const matchRule = findMatchingRule(
            this.segmentRules,
            this.orConditions
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
              this.orConditions
            );
            return matchRule.rule_type;
          }
          return selectedRule ? selectedRule.rule_type : [];
        } else if (
          [
            "loyalty.total_loyalty_earned",
            "loyalty.total_giftcard_amount",
            "loyalty.loyalty_balance",
            "loyalty.giftcard_balance",
          ].includes(this.selectedRuleNameKey)
        ) {
          if (selectedRule) {
            return selectedRule.rule_type;
          } else if (this.selectedRuleName) {
            const matchRule = findMatchingRule(
              this.segmentRules,
              this.orConditions
            );
            return matchRule.rule_type;
          }
        } else {
          return [];
        }
      } else {
        return [];
      }
    },
    /*SHOW / HIDE OPERATION SELECTION BOX*/
    shouldShowOperationInput() {
      return (
        this.selectedOperation === null ||
        this.selectedRuleName === 'Loyalty Tier' ||
        this.selectedRuleName == 'Others' ||
        [
          "is",
          "is not",
          "is(in range)",
          "Total",
          "Balance",
          "Average of all",
          "is(year ignored)",
        ].includes(this.selectedCondition)
      );
    },
    /*SHOW / HIDE INPUT NUMBER ON WITHIN AND NOT WITHIN SELECTION BOX*/
    shouldShowNumberInputThree() {
      return (
        this.selectedCondition !== "" &&
        ([
          "within last",
          "not within last",
          "days since last purchase",
        ].includes(this.selectedCondition) ||
          this.selectedCondition === "")
      );
    },
    /*SHOW / HIDE INPUT TEXT*/
    shouldShowStringValueInput() {
      return (
        this.selectedRuleName === null ||
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
          ["After", "Before"].includes(this.selectedOperation)) //Puchase Date
      );
    },
    /*SHOW / HIDE INPUT DATE IN RANGE*/
    shouldShowDateTwoInput() {
      return (
        this.selectedOperation != null &&
        ["date_range"].includes(this.selecteOpeKey)
      );
    },
    /*SHOW / HIDE INPUT SELECTION IF DAY/WEEK/YEAR*/
    shouldShowTimePeriodSelect() {
      return this.selectedCondition !== null && this.getRuleType.length !== 0;
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
          ["Between"].includes(this.selectTimePeriod))
      );
    },
    inputNumThreeColor() {
      return parseFloat(this.inputNumThreeValue) >= 0 && this.inputNumThreeValue != '' ? '#7367F0' : 'red';
    },
    inputNumTwoColor() {
      return parseFloat(this.inputNumTwoValue) >= 0 && this.inputNumTwoValue != '' ? '#7367F0' : 'red';
    },
    inputNumOneColor() {
      return parseFloat(this.inputNumOneValue) >= 0 && this.inputNumOneValue != '' ? '#7367F0' : 'red';
    },
    inputTextColor() {
      return (this.inputStringValue) != '' ? '' : 'red'
    }
  },
  watch: {
    /*SET DEFAULT RULE CONDITION ON RULE NAME AND GET RULE NAMEW*/
    selectedRuleName() {
      if (Array.isArray(this.selectedRuleName)) {
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
      this.emitSelection();
    },
    /*SET DEFAULT RULE OPERATION ON RULE NAME GET CONDITON VALUE*/
    selectedCondition(newCondition, oldCondition) {
      if (oldCondition && oldCondition != newCondition) {
        this.selectedOperation = this.getRuleOperation[0];
      }
      this.emitSelection();
    },
    /* GET OPERATION VALUE*/
    selectedOperation(newOperation, oldOperation) {
      if (oldOperation && oldOperation != newOperation) {
        this.selectTimePeriod = this.getRuleType[0];
      }
      this.emitSelection();
    },
    /* GET INPUT STRING VALUE*/
    inputStringValue() {
      this.emitSelection();
    },
    /* GET DATE ONE VALUE*/
    inputDateOneValue() {
      this.emitSelection();
    },
    /* GET DATE TWO VALUE*/
    inputDateTwoValue() {
      this.emitSelection();
    },
    /* GET Num ONE VALUE*/
    inputNumOneValue() {
      this.emitSelection();
    },
    /* GET Num TWO VALUE*/
    inputNumTwoValue() {
      this.emitSelection();
    },
    /* GET Num THREE VALUE*/
    inputNumThreeValue() {
      this.emitSelection();
    },
    /* GET SELECTION TIME PERIOD VALUE*/
    selectTimePeriod(newTime, oldTime) {
      if (!newTime) {
        this.selectTimePeriod = this.getRuleType[0];
      }
      this.emitSelection();
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
    if (this.selectedRuleName == 'Loyalty Tier' && this.$route.params.id == 'new') {
      this.loyaltyTiers = this.getRuleOperation
    }
    else if (this.selectedRuleName == 'Others') {
      console.log('selectedRuleName', this.selectedRuleName, this.segmentRules);
    }
    this.allUDf = JSON.parse(localStorage.getItem('customer-udfs'))
    this.purchaseUDFall = JSON.parse(localStorage.getItem('purchase-udfs'))
  }
}
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
  } else if (["loyalty.total_loyalty_earned", ""].includes(s_rule_key)) {
    conditionMap = {
      ">": "Total|Points|More than",
      "<": "Total|Points|Less than",
      "=": "Total|Points|Equal to",
      between: "Total|Points|Between",
      none: "Total|Points|Empty",
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
  <v-row class="align-center" v-if="or_index !== 1 || rule_id !== 4 || rule_id !== 2">
    <v-snackbar v-model="showError" :timeout="timeout" color="red-darken-1" location="top center">
      {{ text }}

      <template v-slot:actions>
        <v-btn color="black" variant="text" @click="showError = false">
          Close
        </v-btn>
      </template>
    </v-snackbar>
    <v-col cols="12" md="1" v-if="or_index !== 0" class="d-flex justify-center">
      <!-- OR BUTTON TO ADD NEW OR CONDITION -->
      <v-chip @click="addOrCondition" size="small" variant="outlined" style="border: 1px solid #7367f0"
        class="px-4 py-3 rule-or-btn justify-center" label>
        <span class="text-indigo-darken-1 text-subtitle-2">OR </span>
      </v-chip>
    </v-col>
    <v-col cols="12" md="4" class="d-flex gap-2" :class="{ 'last-or-condition': isLastOrCondition }">
      <AppSelect placeholder="Rules" class="rule-border text-ellipsis" :items="ruleNames" v-model="selectedRuleName" />
      <AppSelect placeholder="is" class="rule-border text-ellipsis" :items="udfList" v-model="selectedCondition"
        v-if="selectedRuleName == 'Others' && orConditions.panel_id == 1" />
      <AppSelect placeholder="is" class="rule-border text-ellipsis" :items="purchaseudfList" v-model="selectedCondition"
        v-else-if="selectedRuleName == 'Others' && orConditions.panel_id == 3" />
      <AppSelect placeholder="is" class="rule-border text-ellipsis" :items="getRuleConditions"
        v-model="selectedCondition" v-else-if="selectedRuleName != 'Others'" />
    </v-col>
    <v-col cols="12" md="3" :class="{ 'last-or-condition': isLastOrCondition }"
      class="d-flex gap-2 py-4 operation-field" v-if="showCol">
      <AppSelect placeholder="operation" class="rule-border text-ellipsis" :items="getRuleOperation"
        v-model="selectedOperation" v-if="shouldShowOperationInput && selectedRuleName != 'Loyalty Tier'" />
      <AppSelect placeholder="operation" class="rule-border text-ellipsis" :items="loyaltyTiers"
        v-model="selectedOperation" v-else-if="shouldShowOperationInput && selectedRuleName == 'Loyalty Tier'" />
      <v-text-field placeholder="Value 3" class="rule-border" type="number" v-if="shouldShowNumberInputThree"
        v-model="inputNumThreeValue" min="0" :color="inputNumThreeColor"
        :class="inputNumThreeColor === 'red' ? 'rule-border-red' : 'rule-border'"></v-text-field>
    </v-col>
    <v-col cols="12" md="4" class="d-flex align-center gap-2" :class="{ 'last-or-condition': isLastOrCondition }">
      <v-text-field placeholder="Value" type="text" class="" v-if="shouldShowStringValueInput"
        v-model="inputStringValue" :class="inputTextColor === 'red' ? 'rule-border-red' : 'rule-border'"
        :color="inputTextColor" />
      <AppDateTimePicker clearable type="text" class="rule-border ma-2" width="120px" placeholder="YYYY-MM-DD"
        v-if="shouldShowDateOneInput" v-model="inputDateOneValue" />
      <AppDateTimePicker clearable type="text" class="rule-border ma-2" width="120px" placeholder="YYYY-MM-DD"
        v-if="shouldShowDateTwoInput" v-model="inputDateTwoValue" />
      <AppSelect placeholder="Value" class="rule-border text-ellipsis w-25" :items="getRuleType"
        v-if="shouldShowTimePeriodSelect" v-model="selectTimePeriod" />
      <v-text-field placeholder="Value" class="w-25 rule-border" type="number" v-if="shouldShowNumOneInput"
        v-model="inputNumOneValue" min="0" :color="inputNumOneColor"
        :class="inputNumOneColor === 'red' ? 'rule-border-red' : 'rule-border'"></v-text-field>

      <v-text-field placeholder="Value" class="w-25 rule-border" type="number" v-if="shouldShowNumTwoInput"
        v-model="inputNumTwoValue" min="0" :color="inputNumTwoColor"
        :class="inputNumTwoColor === 'red' ? 'rule-border-red' : 'rule-border'"></v-text-field>

      <div v-if="or_index != 0">
        <v-icon @click="onRemove" icon="tabler-trash" color="#7367F0" size="24"></v-icon>
      </div>

      <div v-else>
        <v-btn color="error" prepend-icon="tabler-trash-x" v-if="OrLength < 3" @click="removeSet"> Remove</v-btn>
      </div>
    </v-col>
  </v-row>
</template>

<style scoped>
.rule-bg-white {
  background-color: #f1f0f8;
}

.rule-or-btn {
  color: rgb(115, 103, 240);
  border: 1px solid white;
}

.last-or-condition {
  opacity: 0.3;
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
  border: 1px solid red;
  border-radius: 8px;
}

.custom-snackbar {
  position: fixed;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
}
</style>
