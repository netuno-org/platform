import { createLocalOrUTC } from './from-anything';

export function createUTC(input, format, locale, strict) {
    return createLocalOrUTC(input, format, locale, strict, true).utc();
}

//# sourceMappingURL=utc-compiled.js.map