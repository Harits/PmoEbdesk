---
name: Executive Oversight System
colors:
  surface: '#f8f9fa'
  surface-dim: '#d9dadb'
  surface-bright: '#f8f9fa'
  surface-container-lowest: '#ffffff'
  surface-container-low: '#f3f4f5'
  surface-container: '#edeeef'
  surface-container-high: '#e7e8e9'
  surface-container-highest: '#e1e3e4'
  on-surface: '#191c1d'
  on-surface-variant: '#454652'
  inverse-surface: '#2e3132'
  inverse-on-surface: '#f0f1f2'
  outline: '#767683'
  outline-variant: '#c6c5d4'
  surface-tint: '#4c56af'
  primary: '#000666'
  on-primary: '#ffffff'
  primary-container: '#1a237e'
  on-primary-container: '#8690ee'
  inverse-primary: '#bdc2ff'
  secondary: '#4555b7'
  on-secondary: '#ffffff'
  secondary-container: '#8999ff'
  on-secondary-container: '#182a8e'
  tertiary: '#001e24'
  on-tertiary: '#ffffff'
  tertiary-container: '#00353d'
  on-tertiary-container: '#00a6bc'
  error: '#ba1a1a'
  on-error: '#ffffff'
  error-container: '#ffdad6'
  on-error-container: '#93000a'
  primary-fixed: '#e0e0ff'
  primary-fixed-dim: '#bdc2ff'
  on-primary-fixed: '#000767'
  on-primary-fixed-variant: '#343d96'
  secondary-fixed: '#dee0ff'
  secondary-fixed-dim: '#bbc3ff'
  on-secondary-fixed: '#000e5e'
  on-secondary-fixed-variant: '#2c3c9e'
  tertiary-fixed: '#a1efff'
  tertiary-fixed-dim: '#44d8f1'
  on-tertiary-fixed: '#001f25'
  on-tertiary-fixed-variant: '#004e59'
  background: '#f8f9fa'
  on-background: '#191c1d'
  surface-variant: '#e1e3e4'
typography:
  display-lg:
    fontFamily: Inter
    fontSize: 57px
    fontWeight: '700'
    lineHeight: 64px
    letterSpacing: -0.25px
  headline-lg:
    fontFamily: Inter
    fontSize: 32px
    fontWeight: '600'
    lineHeight: 40px
  headline-lg-mobile:
    fontFamily: Inter
    fontSize: 28px
    fontWeight: '600'
    lineHeight: 36px
  title-lg:
    fontFamily: Inter
    fontSize: 22px
    fontWeight: '500'
    lineHeight: 28px
  body-lg:
    fontFamily: Inter
    fontSize: 16px
    fontWeight: '400'
    lineHeight: 24px
  body-md:
    fontFamily: Inter
    fontSize: 14px
    fontWeight: '400'
    lineHeight: 20px
  label-lg:
    fontFamily: Inter
    fontSize: 12px
    fontWeight: '600'
    lineHeight: 16px
    letterSpacing: 0.5px
  data-mono:
    fontFamily: Inter
    fontSize: 14px
    fontWeight: '600'
    lineHeight: 20px
    letterSpacing: 0.1px
rounded:
  sm: 0.25rem
  DEFAULT: 0.5rem
  md: 0.75rem
  lg: 1rem
  xl: 1.5rem
  full: 9999px
spacing:
  base: 8px
  xs: 4px
  sm: 8px
  md: 16px
  lg: 24px
  xl: 32px
  container-max: 1440px
  gutter: 24px
---

## Brand & Style
This design system is engineered for high-stakes decision-making environments. It adopts the **Material Design 3 (MD3) Expressive** style, which balances the systematic rigor of traditional Material Design with increased character through larger typography scales, intentional whitespace, and sophisticated motion.

The brand personality is **authoritative, transparent, and decisive**. It aims to evoke a sense of "calm control" for the Board of Directors, transforming complex project management data into actionable insights. The UI follows an **Inverse Pyramid** information hierarchy: high-level KPIs at the top, followed by trend visualizations, and concluding with granular project status details.

Key stylistic markers include:
- **Tonal Surfaces:** Extensive use of surface tinting to define hierarchy.
- **Strategic Density:** High density for data tables, low density for executive summaries.
- **Expressive Motion:** Staggered entrance animations for dashboard tiles to signify data freshness.

## Colors
The palette is rooted in **Deep Navy (#1A237E)**, symbolizing stability and institutional trust. This color is reserved for primary actions and global navigation. 

We utilize a "Semantic First" approach for the dashboard:
- **Primary/On-Primary:** Deep Navy is used for the "Source of Truth" elements.
- **Neutral Backgrounds:** #F8F9FA provides a crisp, clinical canvas that eliminates visual noise.
- **Semantic Status:** Red, Amber, and Green are applied with high saturation to ensure immediate cognitive recognition of project health. 
- **Surface Tints:** Use 5-8% opacity overlays of the Primary color on white backgrounds to create distinct content "zones" without using heavy borders.

## Typography
**Inter** is the sole typeface, selected for its exceptional legibility in data-heavy environments and its neutral, professional character. 

Hierarchy is established through weight rather than decorative shifts:
- **Executive Summaries:** Use `display-lg` for single-number KPIs (e.g., Total Budget).
- **Data Points:** Use `data-mono` (utilizing Inter's tabular num features) for financial figures to ensure alignment in columns.
- **Section Headers:** Use `headline-lg` with the Primary Navy color to anchor different sections of the Inverse Pyramid.
- **Labels:** Use `label-lg` in all-caps for metadata such as timestamps or category tags.

## Layout & Spacing
The system utilizes a **12-column fluid grid** for the main dashboard, transitioning to a single-column layout for mobile. 

- **The Inverse Pyramid Model:** 
    - **Tier 1 (Top):** 4-column spans for high-level KPI cards. 
    - **Tier 2 (Middle):** 6-column spans for trend charts and regional heatmaps.
    - **Tier 3 (Bottom):** 12-column spans for detailed project lists and data tables.
- **Margins:** A generous 32px outer margin on desktop ensures the content feels "presented" rather than "crammed."
- **Gaps:** Standardize on a 24px gutter to maintain clear separation between card-based containers.

## Elevation & Depth
In accordance with MD3 Expressive, depth is conveyed through **tonal elevation** supplemented by soft ambient shadows. 

- **Level 0 (Background):** #F8F9FA.
- **Level 1 (Cards):** Pure White (#FFFFFF) with a very soft shadow (Y: 2px, Blur: 8px, 4% Black). This is the default state for project cards.
- **Level 2 (Hover/Active):** Pure White with an increased shadow (Y: 4px, Blur: 12px, 8% Black) and a 1px Primary Navy border at 10% opacity.
- **Glassmorphism:** Use only for "Filter" drawers or side-panels, using a backdrop blur (12px) to maintain context of the dashboard underneath.

## Shapes
The design system follows a **rounded (0.5rem)** logic to soften the analytical nature of the data, making the platform feel modern and accessible.

- **Small Components:** Checkboxes and small buttons use `rounded-sm` (4px).
- **Medium Components:** Main action buttons and input fields use `rounded-md` (8px).
- **Large Components:** Dashboard cards and modal containers use `rounded-lg` (16px) or `rounded-xl` (24px) to create the signature MD3 container look.

## Components
- **Elevated Cards:** The foundational unit. Must include a header area with a `title-lg` and an optional "More" icon button.
- **Status Chips:** Pill-shaped with low-opacity backgrounds of the status color (e.g., Red at 10% opacity) and high-contrast text for accessibility.
- **Segmented Controls:** Used for toggling dashboard views (e.g., "Monthly" vs "Quarterly"). Use the Primary Navy for the selected state.
- **Data Tables:** Borderless design. Use `surface-tint` on alternating rows (Zebra striping) only when data density exceeds 15 rows.
- **Primary Buttons:** High-emphasis, fully rounded (pill-shaped) using the Primary Navy background and White text.
- **KPI Indicators:** Large font weights paired with a "Trend Icon" (Up/Down arrow) color-coded to the semantic status.
- **Input Fields:** Filled style (MD3) with a bottom-stroke, utilizing #F1F3F4 as the fill color to contrast against the white cards.