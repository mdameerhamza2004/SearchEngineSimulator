const puppeteer = require('puppeteer');

(async () => {
  try {
    const browser = await puppeteer.launch();
    const page = await browser.newPage();
    
    page.on('console', msg => {
      console.log('BROWSER_CONSOLE:', msg.type(), msg.text());
    });
    
    page.on('pageerror', err => {
      console.log('BROWSER_ERROR:', err.message);
    });

    console.log('Navigating to http://localhost:5173/ ...');
    await page.goto('http://localhost:5173/', { waitUntil: 'networkidle2', timeout: 10000 });
    
    await new Promise(r => setTimeout(r, 2000));
    
    await browser.close();
    console.log('Done.');
  } catch (err) {
    console.error('PUPPETEER_ERROR:', err);
  }
})();
