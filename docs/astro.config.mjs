import { defineConfig } from 'astro/config';
import starlight from '@astrojs/starlight';

export default defineConfig({
	integrations: [
		starlight({
			title: '',
			logo: {
				src: './src/assets/gatling-sfn-ext.png',
			},
			social: {
				github: 'https://github.com/jossmoff/gatling-stepfunction-extension',
			},
			sidebar: [
				{
					label: 'Getting Started',
					items: [
						// Each item here is one entry in the navigation menu.
						{ label: 'Installation', link: '/getting-started/installation' },
						{ label: 'Quickstart', link: '/getting-started/quickstart' },
					],
				},
				{
					label: 'Reference',
					items: [
						// Each item here is one entry in the navigation menu.
						{ label: 'Standard vs. Express Workflows', link: '/reference/standard-vs-express' },
						{ label: 'Start Executions', link: '/reference/start-executions' },
						{ label: 'Check Execution Success', link: '/reference/check-execution-success' },
					]
				},
			],
		}),
	],

	// Process images with sharp: https://docs.astro.build/en/guides/assets/#using-sharp
	image: { service: { entrypoint: 'astro/assets/services/sharp' } },
});
