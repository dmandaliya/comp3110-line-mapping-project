#!/usr/bin/env bash

# Custom group-test runner for COMP-3110 project.
# This script runs 5 test pairs in a flat "tests" folder.
#
# EXPECTED FILES:
#   tests/TMTasks_1.java       (old)
#   tests/TaskManager.java     (new)
#   tests/TMTasks.xml          (gold)
#
#   tests/UserProfile_1.java   (old)
#   tests/UserProfile_2.java   (new)
#   tests/UserProfile.xml      (gold)
#
#   tests/OrderService.java    (old)
#   tests/OrderService_2.java  (new)
#   tests/OrderService.xml     (gold)
#
#   tests/MathUtils.java       (old)
#   tests/MathUtils_2.java     (new)
#   tests/MathUtils.xml        (gold)
#
#   tests/LogFormatter.java    (old)
#   tests/LogFormatter_2.java  (new)
#   tests/LogFormatter.xml     (gold)
#
# OUTPUT:
#   custom_outputs/<name>.map


set -e

OUT="custom_outputs"
mkdir -p "$OUT"

run_test() {
    local name="$1"
    local old="$2"
    local new="$3"
    local gold="tests/${name}.xml"
    local out="${OUT}/${name}.map"

    echo "=============================="
    echo "Running custom test: $name"
    echo "=============================="

    # Validate files exist
    if [[ ! -f "$old" ]]; then
        echo " ERROR: Missing old file: $old"
        return
    fi
    if [[ ! -f "$new" ]]; then
        echo " ERROR: Missing new file: $new"
        return
    fi
    if [[ ! -f "$gold" ]]; then
        echo " ERROR: Missing XML gold file: $gold"
        return
    fi

    echo " Running LineMappingTool..."
    java -cp src LineMappingTool "$old" "$new" > "$out"

    echo " Evaluating..."
    # Correct order: Evaluator <toolOutput> <goldXml>
    java -cp src Evaluator "$out" "$gold"

    echo
}

# 1) TMTasks → TMTasks_1.java -> TaskManager.java
run_test "TMTasks" \
    "tests/TMTasks_1.java" \
    "tests/TaskManager.java"

# 2) UserProfile
run_test "UserProfile" \
    "tests/UserProfile_1.java" \
    "tests/UserProfile_2.java"

# 3) OrderService
run_test "OrderService" \
    "tests/OrderService.java" \
    "tests/OrderService_2.java"

# 4) MathUtils
run_test "MathUtils" \
    "tests/MathUtils.java" \
    "tests/MathUtils_2.java"

# 5) LogFormatter
run_test "LogFormatter" \
    "tests/LogFormatter.java" \
    "tests/LogFormatter_2.java"


#6) run_test "NewFeature" \
    "tests/NewFeature_1.java" \
    "tests/NewFeature_2.java"


