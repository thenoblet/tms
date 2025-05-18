/**
 * Initialize delete confirmation for all delete actions
 */
document.addEventListener('DOMContentLoaded', function() {
    initializeDeleteConfirmation();
    initializeDateRestrictions();
    initializeDropdowns();
    initializeFormValidation();
});

/**
 * Complete rewrite of the delete confirmation to fix the double prompt
 * Using a variable to track if confirmation is in progress
 */
let deleteConfirmationInProgress = false;

function initializeDeleteConfirmation() {
    document.addEventListener('click', function(e) {
        if (deleteConfirmationInProgress) {
            return;
        }

        const deleteLink = e.target.closest('a.dropdown-item.delete');
        if (!deleteLink) {
            return;
        }

        e.preventDefault();
        e.stopPropagation();

        deleteConfirmationInProgress = true;

        // Get task details, clean up the title if needed
        const taskId = deleteLink.dataset.taskId;
        let taskTitle = deleteLink.dataset.taskTitle || 'this task';

        if (taskTitle && typeof taskTitle === 'string' && taskTitle.endsWith("')")) {
            taskTitle = taskTitle.substring(0, taskTitle.length - 2);
        }

        if (confirm(`Are you sure you want to delete "${taskTitle}"?`)) {
            setTimeout(function() {
                window.location.href = `tasks?action=delete&id=${taskId}`;
            }, 10);
        } else {
            deleteConfirmationInProgress = false;
        }

        setTimeout(function() {
            deleteConfirmationInProgress = false;
        }, 2000);
    });
}

/**
 * Toggle a single dropdown menu
 */
function toggleDropdown(button) {
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
 * Enhanced form validation
 */
function initializeFormValidation() {
    const form = document.querySelector('.needs-validation');

    if (!form)
        return;

    const requiredFields = form.querySelectorAll('[required]');

    // Validates a single field
    function validateField(field) {
        if (!field.checkValidity()) {
            field.classList.add('is-invalid');
            field.classList.remove('is-valid');

            const formGroup = field.closest('.mb-3');
            if (formGroup) {
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

    form.addEventListener('submit', function(event) {
        let formValid = true;

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
 * For direct HTML onclick calls - this avoids double confirmation
 */
function confirmDelete(taskId, taskTitle) {
    if (deleteConfirmationInProgress) {
        return false;
    }

    deleteConfirmationInProgress = true;

    window.location.href = `tasks?action=delete&id=${taskId}`;

    setTimeout(function() {
        deleteConfirmationInProgress = false;
    }, 2000);

    return false;
}