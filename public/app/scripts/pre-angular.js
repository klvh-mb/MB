if (location.hash.match(/^#[^!]/)) { 
    location.hash = location.hash.replace(/^#/, "#!");
}