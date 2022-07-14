/**
 * determine required receipt-entry-fields is filled
 * @returns {boolean}
 */
function isFilled() {
    let box = document.getElementById('receipt-entry-fields').getElementsByTagName('input');
    for(let i=0; i < box.length; i++){
        if( (box[i].value === null || box[i].value === '') && (box[i].hasAttribute('aria-required') || box[i].hasAttribute('required'))){
            box[i].style.borderColor = "red";
            return false;
        }
    }
    return true;
}