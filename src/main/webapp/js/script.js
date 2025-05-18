document.addEventListener('DOMContentLoaded', function() {
    initializeDeleteConfirmation();
    initializeDateRestrictions();
    initializeDropdowns();
    initializeFormValidation(); // Added the new function call here
});

/**
 * Initialize delete confirmation for all delete actions
 */
document.addEventListener('DOMContentLoaded', function() {
    initializeDeleteConfirmation();
    initializeDateRestrictions();
    initializeDropdowns();
    initializeFormValidation(); // Added the new function call here
});

/**
 * Initialize delete confirmation for all delete actions
 */
function initializeDeleteConfirmation() {
    // Use event delegation with a more specific selector
    document.addEventListener('click', function(e) {
        const deleteLink = e.target.closest('a.dropdown-item.delete');
        if (deleteLink) {
            e.preventDefault();
            e.stopPropagation(); // Prevent event from bubbling up

            // Check if we've already handled this click
            if (deleteLink.dataset.confirmed) {
                delete deleteLink.dataset.confirmed;
                return;
            }

            const taskId = deleteLink.dataset.taskId;
            const taskTitle = deleteLink.dataset.taskTitle;

            if (confirm(`Are you sure you want to delete "${taskTitle}"?`)) {
                deleteLink.dataset.confirmed = 'true';
                window.location.href = `tasks?action=delete&id=${taskId}`;
            }
        }
    });

    // Remove the form-based delete confirmation if you're not using it
    // Or keep it if you have forms that need it
}

/**
 * Set date restrictions for due date fields
 */
function initializeDateRestrictions() {
    const dueDateInput = document.getElementById('dueDate');
    if (dueDateInput) {
        const today = new Date();
        today.setHours(0, 0, 0, 0);
        dueDateInput.min = today.toISOString().split('T')[0];
    }
}

/**
 * Initialize all dropdown menus
 */
function initializeDropdowns() {
    // Add click handlers to all dropdown toggles
    document.querySelectorAll('.task-status .task-actions .btn-more').forEach(button => {
        button.addEventListener('click', function(e) {
            toggleDropdown(this);
            e.stopPropagation();
        });
    });

    // Close dropdowns when clicking elsewhere
    document.addEventListener('click', function(e) {
        if (!e.target.closest('.task-actions')) {
            closeAllDropdowns();
        }
    });
}

/**
 * Toggle a single dropdown menu
 */
function toggleDropdown(button) {
    // Find the dropdown - it could be next sibling or within the clicked element
    let dropdown;
    if (button.classList.contains('task-status')) {
        dropdown = button.querySelector('.dropdown-menu');
    } else {
        dropdown = button.nextElementSibling;
    }

    const isShowing = dropdown.classList.contains('show');

    closeAllDropdowns();

    if (!isShowing) {
        dropdown.classList.add('show');
    }
}

/**
 * Close all dropdown menus
 */
function closeAllDropdowns() {
    document.querySelectorAll('.dropdown-menu').forEach(menu => {
        menu.classList.remove('show');
    });
}

/**
 * Confirm before deletion (for direct link calls)
 */
function confirmDelete(taskId, taskTitle) {
    if (confirm(`Are you sure you want to delete "${taskTitle}"?`)) {
        window.location.href = `tasks?action=delete&id=${taskId}`;
    }
    return false;
}

/**
 * Enhanced form validation
 */
function initializeFormValidation() {
    const form = document.querySelector('.needs-validation');

    // If no validation form on the page, exit early
    if (!form) return;

    const requiredFields = form.querySelectorAll('[required]');

    // Function to validate a single field
    function validateField(field) {
        if (!field.checkValidity()) {
            field.classList.add('is-invalid');
            field.classList.remove('is-valid');

            // Find the closest parent with mb-3 class (Bootstrap form group)
            const formGroup = field.closest('.mb-3');
            if (formGroup) {
                // Make sure the feedback message is visible
                let feedback = formGroup.querySelector('.invalid-feedback');
                if (!feedback) {
                    feedback = document.createElement('div');
                    feedback.className = 'invalid-feedback';
                    feedback.textContent = field.validationMessage || 'This field is required';
                    formGroup.appendChild(feedback);
                }
            }
            return false;
        } else {
            field.classList.remove('is-invalid');
            field.classList.add('is-valid');
            return true;
        }
    }

    // Add input event listeners to all required fields
    requiredFields.forEach(function(field) {
        field.addEventListener('input', function() {
            validateField(field);
        });

        field.addEventListener('blur', function() {
            validateField(field);
        });
    });

    // Radio buttons require special handling
    const radioGroups = {};
    form.querySelectorAll('input[type="radio"][required]').forEach(function(radio) {
        const name = radio.getAttribute('name');
        if (!radioGroups[name]) {
            radioGroups[name] = true;

            // Find all radios with the same name
            const radios = form.querySelectorAll(`input[name="${name}"]`);
            radios.forEach(function(r) {
                r.addEventListener('change', function() {
                    // Validate the entire radio group
                    const isValid = Array.from(radios).some(r => r.checked);
                    radios.forEach(function(r) {
                        if (isValid) {
                            r.classList.remove('is-invalid');
                        } else {
                            r.classList.add('is-invalid');
                        }
                    });

                    // Update feedback message
                    const formGroup = r.closest('.mb-3');
                    if (formGroup) {
                        let feedback = formGroup.querySelector('.invalid-feedback');
                        if (!feedback) {
                            feedback = document.createElement('div');
                            feedback.className = 'invalid-feedback';
                            feedback.textContent = 'Please select an option';
                            formGroup.appendChild(feedback);
                        }

                        if (isValid) {
                            feedback.style.display = 'none';
                        } else {
                            feedback.style.display = 'block';
                        }
                    }
                });
            });
        }
    });

    // Main form submission handler
    form.addEventListener('submit', function(event) {
        let formValid = true;

        // Validate all required fields
        requiredFields.forEach(function(field) {
            if (!validateField(field)) {
                formValid = false;
            }
        });

        // Validate radio groups
        Object.keys(radioGroups).forEach(function(name) {
            const radios = form.querySelectorAll(`input[name="${name}"]`);
            const isValid = Array.from(radios).some(r => r.checked);

            if (!isValid) {
                formValid = false;
                radios.forEach(r => r.classList.add('is-invalid'));

                // Show feedback for radio groups
                const formGroup = radios[0].closest('.mb-3');
                if (formGroup) {
                    let feedback = formGroup.querySelector('.invalid-feedback');
                    if (!feedback) {
                        feedback = document.createElement('div');
                        feedback.className = 'invalid-feedback';
                        feedback.textContent = 'Please select an option';
                        formGroup.appendChild(feedback);
                    }
                    feedback.style.display = 'block';
                }
            }
        });

        // Prevent submission if form is invalid
        if (!formValid) {
            event.preventDefault();
            event.stopPropagation();

            // Scroll to the first invalid element
            const firstInvalid = form.querySelector('.is-invalid');
            if (firstInvalid) {
                firstInvalid.focus();
                firstInvalid.scrollIntoView({behavior: 'smooth', block: 'center'});
            }
        }

        form.classList.add('was-validated');
    }, false);
}
/**
 * Set date restrictions for due date fields
 */
function initializeDateRestrictions() {
    const dueDateInput = document.getElementById('dueDate');
    if (dueDateInput) {
        const today = new Date();
        today.setHours(0, 0, 0, 0);
        dueDateInput.min = today.toISOString().split('T')[0];
    }
}

/**
 * Initialize all dropdown menus
 */
function initializeDropdowns() {
    // Add click handlers to all dropdown toggles
    document.querySelectorAll('.task-status .task-actions .btn-more').forEach(button => {
        button.addEventListener('click', function(e) {
            toggleDropdown(this);
            e.stopPropagation();
        });
    });

    // Close dropdowns when clicking elsewhere
    document.addEventListener('click', function(e) {
        if (!e.target.closest('.task-actions')) {
            closeAllDropdowns();
        }
    });
}

/**
 * Toggle a single dropdown menu
 */
function toggleDropdown(button) {
    // Find the dropdown - it could be next sibling or within the clicked element
    let dropdown;
    if (button.classList.contains('task-status')) {
        dropdown = button.querySelector('.dropdown-menu');
    } else {
        dropdown = button.nextElementSibling;
    }

    const isShowing = dropdown.classList.contains('show');

    closeAllDropdowns();

    if (!isShowing) {
        dropdown.classList.add('show');
    }
}

/**
 * Close all dropdown menus
 */
function closeAllDropdowns() {
    document.querySelectorAll('.dropdown-menu').forEach(menu => {
        menu.classList.remove('show');
    });
}

/**
 * Confirm before deletion (for direct link calls)
 */
function confirmDelete(taskId, taskTitle) {
    if (confirm(`Are you sure you want to delete "${taskTitle}"?`)) {
        window.location.href = `tasks?action=delete&id=${taskId}`;
    }
    return false;
}

/**
 * Enhanced form validation
 */
function initializeFormValidation() {
    const form = document.querySelector('.needs-validation');

    // If no validation form on the page, exit early
    if (!form) return;

    const requiredFields = form.querySelectorAll('[required]');

    // Function to validate a single field
    function validateField(field) {
        if (!field.checkValidity()) {
            field.classList.add('is-invalid');
            field.classList.remove('is-valid');

            // Find the closest parent with mb-3 class (Bootstrap form group)
            const formGroup = field.closest('.mb-3');
            if (formGroup) {
                // Make sure the feedback message is visible
                let feedback = formGroup.querySelector('.invalid-feedback');
                if (!feedback) {
                    feedback = document.createElement('div');
                    feedback.className = 'invalid-feedback';
                    feedback.textContent = field.validationMessage || 'This field is required';
                    formGroup.appendChild(feedback);
                }
            }
            return false;
        } else {
            field.classList.remove('is-invalid');
            field.classList.add('is-valid');
            return true;
        }
    }

    // Add input event listeners to all required fields
    requiredFields.forEach(function(field) {
        field.addEventListener('input', function() {
            validateField(field);
        });

        field.addEventListener('blur', function() {
            validateField(field);
        });
    });

    // Radio buttons require special handling
    const radioGroups = {};
    form.querySelectorAll('input[type="radio"][required]').forEach(function(radio) {
        const name = radio.getAttribute('name');
        if (!radioGroups[name]) {
            radioGroups[name] = true;

            // Find all radios with the same name
            const radios = form.querySelectorAll(`input[name="${name}"]`);
            radios.forEach(function(r) {
                r.addEventListener('change', function() {
                    // Validate the entire radio group
                    const isValid = Array.from(radios).some(r => r.checked);
                    radios.forEach(function(r) {
                        if (isValid) {
                            r.classList.remove('is-invalid');
                        } else {
                            r.classList.add('is-invalid');
                        }
                    });

                    // Update feedback message
                    const formGroup = r.closest('.mb-3');
                    if (formGroup) {
                        let feedback = formGroup.querySelector('.invalid-feedback');
                        if (!feedback) {
                            feedback = document.createElement('div');
                            feedback.className = 'invalid-feedback';
                            feedback.textContent = 'Please select an option';
                            formGroup.appendChild(feedback);
                        }

                        if (isValid) {
                            feedback.style.display = 'none';
                        } else {
                            feedback.style.display = 'block';
                        }
                    }
                });
            });
        }
    });

    // Main form submission handler
    form.addEventListener('submit', function(event) {
        let formValid = true;

        // Validate all required fields
        requiredFields.forEach(function(field) {
            if (!validateField(field)) {
                formValid = false;
            }
        });

        // Validate radio groups
        Object.keys(radioGroups).forEach(function(name) {
            const radios = form.querySelectorAll(`input[name="${name}"]`);
            const isValid = Array.from(radios).some(r => r.checked);

            if (!isValid) {
                formValid = false;
                radios.forEach(r => r.classList.add('is-invalid'));

                // Show feedback for radio groups
                const formGroup = radios[0].closest('.mb-3');
                if (formGroup) {
                    let feedback = formGroup.querySelector('.invalid-feedback');
                    if (!feedback) {
                        feedback = document.createElement('div');
                        feedback.className = 'invalid-feedback';
                        feedback.textContent = 'Please select an option';
                        formGroup.appendChild(feedback);
                    }
                    feedback.style.display = 'block';
                }
            }
        });

        // Prevent submission if form is invalid
        if (!formValid) {
            event.preventDefault();
            event.stopPropagation();

            // Scroll to the first invalid element
            const firstInvalid = form.querySelector('.is-invalid');
            if (firstInvalid) {
                firstInvalid.focus();
                firstInvalid.scrollIntoView({behavior: 'smooth', block: 'center'});
            }
        }

        form.classList.add('was-validated');
    }, false);
}