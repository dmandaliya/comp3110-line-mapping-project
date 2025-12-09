#!/bin/bash

# Where your Java classes live
CP=src

# List of all test prefixes (before _1.java / _2.java / .xml)
TESTS=(
  ArrayReference
  asdf
  ASTResolving
  BaseTypes
  BuildPathsPropertyPage
  CompilationUnitDocumentProvider
  CPListLabelProvider
  DeltaProcessor
  DialogCustomize
  DirectoryDialog
  DoubleCache
  FontData
  GC
  GC2
  JavaCodeScanner
  JavaModelManager
  JavaPerspectiveFactory
  PluginSearchScope
  RefreshLocal
  ResourceCompareInput
  ResourceInfo
  SaveManager
  TabFolder
)

mkdir -p out

for T in "${TESTS[@]}"; do
  echo "=============================="
  echo "Running test: $T"
  echo "=============================="

  OLD="EclipseTest/${T}_1.java"
  NEW="EclipseTest/${T}_2.java"
  GOLD="EclipseTest/${T}.xml"
  OUT="out/${T}_deep.out"

  if [[ ! -f "$OLD" || ! -f "$NEW" || ! -f "$GOLD" ]]; then
    echo "  [SKIP] Missing one of: $OLD / $NEW / $GOLD"
    echo
    continue
  fi

  # 1) Run your line mapping tool
  java -cp "$CP" LineMappingTool "$OLD" "$NEW" > "$OUT"

  # 2) Evaluate against the gold XML
  echo "  Evaluating with Evaluator..."
  java -cp "$CP" Evaluator "$OUT" "$GOLD"
  echo
done
