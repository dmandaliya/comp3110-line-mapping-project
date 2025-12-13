#!/bin/bash
set -e

mkdir -p groupTestResults

run_test () {
  NAME=$1
  OLD=$2
  NEW=$3
  XML=$4

  echo "=============================="
  echo "Running group pair: $NAME"
  echo "=============================="
  echo "  Running LineMappingTool..."
  java -cp src LineMappingTool "$OLD" "$NEW" "groupTestResults/$NAME.map"
  echo "  Evaluating with Evaluator..."
  cat "groupTestResults/$NAME.map" | java -cp src Evaluator "$XML"
}

# =============================
# DEEP PAIRS (already in tests/)
# =============================
run_test TMTasks        tests/TMTasks_1.java        tests/TaskManager.java        tests/TMTasks.xml
run_test UserProfile    tests/UserProfile_1.java    tests/UserProfile_2.java      tests/UserProfile.xml
run_test OrderService   tests/OrderService.java     tests/OrderService_2.java     tests/OrderService.xml
run_test MathUtils      tests/MathUtils.java        tests/MathUtils_2.java        tests/MathUtils.xml
run_test LogFormatter   tests/LogFormatter.java     tests/LogFormatter_2.java     tests/LogFormatter.xml
run_test NewFeature     tests/NewFeature_1.java     tests/NewFeature.java         tests/NewFeature.xml

# =============================
# ANWAR PAIRS (already in tests/)
# =============================
run_test anmarpair1 tests/anmarOldFile1.java tests/anmarNewFile1.java tests/anmarpair1.xml
run_test anmarpair2 tests/anmarOldFile2.java tests/anmarNewFile2.java tests/anmarpair2.xml
run_test anmarpair3 tests/anmarOldFile3.java tests/anmarNewFile3.java tests/anmarpair3.xml
run_test anmarpair4 tests/anmarOldFile4.java tests/anmarNewFile4.java tests/anmarpair4.xml
run_test anmarpair5 tests/anmarOldFile5.java tests/anmarNewFile5.java tests/anmarpair5.xml

# ===============================
# PRINCE PAIRS: (already in tests/)
# ===============================
run_test MainAcc tests/MainAcc_1.java tests/MainAcc_2.java tests/MainAcc.xml
run_test BankAccount tests/BankAccount_1.java tests/BankAccount_2.java tests/BankAccount.xml
run_test BankAccountTransaction tests/BankAccountTransaction_1.java tests/BankAccountTransaction_2.java tests/BankAccountTransaction.xml
run_test TransactionHistory tests/TransactionHistory_1.java tests/TransactionHistory_2.java tests/TransactionHistory.xml
run_test UserCreate tests/UserCreate_1.java tests/UserCreate_2.java tests/UserCreate.xml

# ===============================
# AMMAR PAIRS:
# ===============================
# run_test amar1 tests/amarOld1.java tests/amarNew1.java tests/amarpair1.xml


# ===============================
# HUZAIFAH PAIRS:
# ===============================
# run_test farmer1 tests/farmerOld1.java tests/farmerNew1.java tests/farmerpair1.xml


echo "=============================="
echo "GROUP DATASET COMPLETED"
echo "=============================="
