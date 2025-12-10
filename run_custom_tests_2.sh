set -e

OUT="custom_outputs"
mkdir -p "$OUT"

run_test() {
    local name="$1"
    local old="$2"
    local new="$3"
    local expected="tests/${name}.xml"
    local out="${OUT}/${name}.map"

    echo "=============================="
    echo "Running test: $name"
    echo "=============================="

    if [[ ! -f "$old" ]]; then
        echo "Missing old file: $old"
        return
    fi
    if [[ ! -f "$new" ]]; then
        echo "Missing new file: $new"
        return
    fi
    if [[ ! -f "$expected" ]]; then
        echo "Missing expected XML: $expected"
        return
    fi

    java -cp src LineMappingTool "$old" "$new" > "$out"
    java -cp src Evaluator "$out" "$expected"

    echo
}


run_test "MainAcc" \
    "tests/MainAcc_1.java" \
    "tests/MainAcc_2.java"

run_test "BankAccount" \
    "tests/BankAccount_1.java" \
    "tests/BankAccount_2.java"

run_test "BankAccountTransaction" \
    "tests/BankAccountTransaction_1.java" \
    "tests/BankAccountTransaction_2.java"

run_test "TransactionHistory" \
    "tests/TransactionHistory_1.java" \
    "tests/TransactionHistory_2.java"

run_test "UserCreate" \
    "tests/UserCreate_1.java" \
    "tests/UserCreate_2.java"
