/**
 * COMP 3110 Line Mapping Tool - Main Application
 * Handles UI interactions and visualization
 */

// Initialize the mapping engine
const mappingEngine = new LineMappingEngine();

// State
let oldFileContent = null;
let newFileContent = null;
let mappingResults = null;
let analysisResults = null;

// DOM Elements
const oldFileInput = document.getElementById('oldFile');
const newFileInput = document.getElementById('newFile');
const oldFileName = document.getElementById('oldFileName');
const newFileName = document.getElementById('newFileName');
const compareBtn = document.getElementById('compareBtn');
const controlsSection = document.getElementById('controlsSection');
const statsSection = document.getElementById('statsSection');
const comparisonSection = document.getElementById('comparisonSection');
const legendSection = document.getElementById('legendSection');

// Control elements
const showMappingsCheckbox = document.getElementById('showMappings');
const highlightChangesCheckbox = document.getElementById('highlightChanges');
const showDeletedCheckbox = document.getElementById('showDeleted');
const showInsertedCheckbox = document.getElementById('showInserted');
const exportBtn = document.getElementById('exportBtn');

// Stats elements
const unchangedCountEl = document.getElementById('unchangedCount');
const changedCountEl = document.getElementById('changedCount');
const deletedCountEl = document.getElementById('deletedCount');
const insertedCountEl = document.getElementById('insertedCount');

// Code view elements
const oldCodeView = document.getElementById('oldCodeView');
const newCodeView = document.getElementById('newCodeView');
const oldLineCountEl = document.getElementById('oldLineCount');
const newLineCountEl = document.getElementById('newLineCount');
const mappingCanvas = document.getElementById('mappingCanvas');

/**
 * File input handlers
 */
oldFileInput.addEventListener('change', (e) => {
    const file = e.target.files[0];
    if (file) {
        oldFileName.textContent = file.name;
        readFile(file, (content) => {
            oldFileContent = content;
            checkReadyToCompare();
        });
    }
});

newFileInput.addEventListener('change', (e) => {
    const file = e.target.files[0];
    if (file) {
        newFileName.textContent = file.name;
        readFile(file, (content) => {
            newFileContent = content;
            checkReadyToCompare();
        });
    }
});

/**
 * Read file content
 */
function readFile(file, callback) {
    const reader = new FileReader();
    reader.onload = (e) => {
        callback(e.target.result);
    };
    reader.readAsText(file);
}

/**
 * Check if ready to compare
 */
function checkReadyToCompare() {
    if (oldFileContent && newFileContent) {
        compareBtn.disabled = false;
    }
}

/**
 * Compare button handler
 */
compareBtn.addEventListener('click', () => {
    performComparison();
});

/**
 * Perform the line mapping comparison
 */
function performComparison() {
    // Split files into lines
    const oldLines = oldFileContent.split('\n');
    const newLines = newFileContent.split('\n');

    // Compute line mappings
    console.log('Computing line mappings...');
    mappingResults = mappingEngine.compute(oldLines, newLines);
    
    // Analyze results
    analysisResults = mappingEngine.analyzeMapping(mappingResults, oldLines, newLines);
    
    console.log('Mapping complete!', analysisResults);

    // Display results
    displayStatistics();
    displayComparison(oldLines, newLines);
    
    // Show sections
    controlsSection.style.display = 'block';
    statsSection.style.display = 'block';
    comparisonSection.style.display = 'block';
    legendSection.style.display = 'block';
}

/**
 * Display statistics
 */
function displayStatistics() {
    const stats = analysisResults.stats;
    
    unchangedCountEl.textContent = stats.unchanged;
    changedCountEl.textContent = stats.changed;
    deletedCountEl.textContent = stats.deleted;
    insertedCountEl.textContent = stats.inserted;
    
    // Animate numbers
    animateValue(unchangedCountEl, 0, stats.unchanged, 800);
    animateValue(changedCountEl, 0, stats.changed, 800);
    animateValue(deletedCountEl, 0, stats.deleted, 800);
    animateValue(insertedCountEl, 0, stats.inserted, 800);
}

/**
 * Animate counter
 */
function animateValue(element, start, end, duration) {
    const range = end - start;
    const increment = range / (duration / 16);
    let current = start;
    
    const timer = setInterval(() => {
        current += increment;
        if ((increment > 0 && current >= end) || (increment < 0 && current <= end)) {
            element.textContent = end;
            clearInterval(timer);
        } else {
            element.textContent = Math.floor(current);
        }
    }, 16);
}

/**
 * Display code comparison
 */
function displayComparison(oldLines, newLines) {
    // Clear previous content
    oldCodeView.innerHTML = '';
    newCodeView.innerHTML = '';
    
    // Update line counts
    oldLineCountEl.textContent = `${oldLines.length} lines`;
    newLineCountEl.textContent = `${newLines.length} lines`;
    
    // Display old file with categories
    const categoryMap = new Map();
    analysisResults.lineCategories.forEach(item => {
        categoryMap.set(item.oldLine, item);
    });
    
    oldLines.forEach((line, index) => {
        const lineNum = index + 1;
        const category = categoryMap.get(lineNum);
        const lineDiv = createCodeLine(lineNum, line, category ? category.category : 'unchanged');
        lineDiv.dataset.oldLine = lineNum;
        oldCodeView.appendChild(lineDiv);
    });
    
    // Display new file
    const newLineCategories = new Map();
    analysisResults.lineCategories.forEach(item => {
        if (item.newLine !== -1) {
            newLineCategories.set(item.newLine, item.category);
        }
    });
    
    newLines.forEach((line, index) => {
        const lineNum = index + 1;
        let category = newLineCategories.get(lineNum);
        
        // Check if this line is inserted
        if (!analysisResults.usedNewLines.has(index)) {
            category = 'inserted';
        }
        
        const lineDiv = createCodeLine(lineNum, line, category || 'unchanged');
        lineDiv.dataset.newLine = lineNum;
        newCodeView.appendChild(lineDiv);
    });
    
    // Draw mapping lines
    drawMappingLines();
}

/**
 * Create a code line element
 */
function createCodeLine(lineNum, content, category) {
    const lineDiv = document.createElement('div');
    lineDiv.className = `code-line ${category}`;
    
    const lineNumSpan = document.createElement('span');
    lineNumSpan.className = 'line-number';
    lineNumSpan.textContent = lineNum;
    
    const contentSpan = document.createElement('span');
    contentSpan.className = 'line-content';
    contentSpan.textContent = content || ' ';
    
    lineDiv.appendChild(lineNumSpan);
    lineDiv.appendChild(contentSpan);
    
    return lineDiv;
}

/**
 * Draw SVG mapping lines
 */
function drawMappingLines() {
    // Clear existing lines
    mappingCanvas.innerHTML = '';
    
    if (!showMappingsCheckbox.checked) return;
    
    const oldViewRect = oldCodeView.getBoundingClientRect();
    const newViewRect = newCodeView.getBoundingClientRect();
    const canvasRect = mappingCanvas.getBoundingClientRect();
    
    const canvasWidth = canvasRect.width;
    const canvasHeight = Math.max(oldViewRect.height, newViewRect.height);
    
    mappingCanvas.setAttribute('height', canvasHeight);
    
    // Draw lines for each mapping
    analysisResults.lineCategories.forEach(item => {
        if (item.newLine === -1) return; // Skip deleted lines
        
        const oldLineEl = oldCodeView.querySelector(`[data-old-line="${item.oldLine}"]`);
        const newLineEl = newCodeView.querySelector(`[data-new-line="${item.newLine}"]`);
        
        if (!oldLineEl || !newLineEl) return;
        
        const oldRect = oldLineEl.getBoundingClientRect();
        const newRect = newLineEl.getBoundingClientRect();
        
        const y1 = oldRect.top - oldViewRect.top + oldRect.height / 2 + oldCodeView.scrollTop;
        const y2 = newRect.top - newViewRect.top + newRect.height / 2 + newCodeView.scrollTop;
        
        const line = document.createElementNS('http://www.w3.org/2000/svg', 'line');
        line.setAttribute('x1', '0');
        line.setAttribute('y1', y1);
        line.setAttribute('x2', canvasWidth);
        line.setAttribute('y2', y2);
        line.setAttribute('class', `mapping-line ${item.category}`);
        
        // Add hover effect
        line.addEventListener('mouseenter', () => {
            oldLineEl.style.background = 'rgba(79, 70, 229, 0.15)';
            newLineEl.style.background = 'rgba(79, 70, 229, 0.15)';
        });
        
        line.addEventListener('mouseleave', () => {
            oldLineEl.style.background = '';
            newLineEl.style.background = '';
        });
        
        mappingCanvas.appendChild(line);
    });
}

/**
 * Control panel event listeners
 */
showMappingsCheckbox.addEventListener('change', () => {
    if (mappingResults) {
        drawMappingLines();
    }
});

highlightChangesCheckbox.addEventListener('change', () => {
    const codeLines = document.querySelectorAll('.code-line');
    codeLines.forEach(line => {
        if (highlightChangesCheckbox.checked) {
            line.style.opacity = '1';
        } else {
            if (line.classList.contains('changed') || line.classList.contains('deleted') || line.classList.contains('inserted')) {
                line.style.opacity = '0.5';
            }
        }
    });
});

showDeletedCheckbox.addEventListener('change', () => {
    const deletedLines = document.querySelectorAll('.code-line.deleted');
    deletedLines.forEach(line => {
        line.style.display = showDeletedCheckbox.checked ? 'flex' : 'none';
    });
    if (mappingResults) {
        drawMappingLines();
    }
});

showInsertedCheckbox.addEventListener('change', () => {
    const insertedLines = document.querySelectorAll('.code-line.inserted');
    insertedLines.forEach(line => {
        line.style.display = showInsertedCheckbox.checked ? 'flex' : 'none';
    });
    if (mappingResults) {
        drawMappingLines();
    }
});

/**
 * Export mapping results
 */
exportBtn.addEventListener('click', () => {
    if (!mappingResults) return;
    
    // Create export data
    let exportText = 'oldLine\tnewLine\n';
    mappingResults.forEach(mapping => {
        exportText += `${mapping.oldLine}\t${mapping.newLine}\n`;
    });
    
    // Create download
    const blob = new Blob([exportText], { type: 'text/plain' });
    const url = URL.createObjectURL(blob);
    const a = document.createElement('a');
    a.href = url;
    a.download = 'line_mapping.txt';
    document.body.appendChild(a);
    a.click();
    document.body.removeChild(a);
    URL.revokeObjectURL(url);
    
    // Show notification
    showNotification('Mapping exported successfully!');
});

/**
 * Show notification
 */
function showNotification(message) {
    const notification = document.createElement('div');
    notification.style.cssText = `
        position: fixed;
        top: 20px;
        right: 20px;
        background: #10b981;
        color: white;
        padding: 1rem 1.5rem;
        border-radius: 0.5rem;
        box-shadow: 0 10px 15px -3px rgba(0, 0, 0, 0.1);
        z-index: 1000;
        animation: slideInRight 0.3s ease-out;
    `;
    notification.textContent = message;
    document.body.appendChild(notification);
    
    setTimeout(() => {
        notification.style.animation = 'slideOutRight 0.3s ease-out';
        setTimeout(() => {
            document.body.removeChild(notification);
        }, 300);
    }, 3000);
}

// Add keyframe animations for notifications
const style = document.createElement('style');
style.textContent = `
    @keyframes slideInRight {
        from {
            transform: translateX(100%);
            opacity: 0;
        }
        to {
            transform: translateX(0);
            opacity: 1;
        }
    }
    
    @keyframes slideOutRight {
        from {
            transform: translateX(0);
            opacity: 1;
        }
        to {
            transform: translateX(100%);
            opacity: 0;
        }
    }
`;
document.head.appendChild(style);

// Handle window resize for mapping lines
let resizeTimeout;
window.addEventListener('resize', () => {
    clearTimeout(resizeTimeout);
    resizeTimeout = setTimeout(() => {
        if (mappingResults) {
            drawMappingLines();
        }
    }, 250);
});

// Handle scroll to update mapping lines
oldCodeView.addEventListener('scroll', () => {
    if (mappingResults && showMappingsCheckbox.checked) {
        drawMappingLines();
    }
});

newCodeView.addEventListener('scroll', () => {
    if (mappingResults && showMappingsCheckbox.checked) {
        drawMappingLines();
    }
});

console.log('Line Mapping Tool initialized!');
